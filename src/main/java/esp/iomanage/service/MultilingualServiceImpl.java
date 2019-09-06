package esp.iomanage.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import esp.iomanage.dao.SccSltnLangMstDao;
import esp.iomanage.dao.SccUiMlangMstDao;
import esp.iomanage.utils.EspFileUtils;
import esp.iomanage.utils.EspSvnUtils;

@Service
public class MultilingualServiceImpl implements MultilingualService {

    private Logger logger = LoggerFactory.getLogger(MultilingualServiceImpl.class);
    
    @Autowired
    private SccSltnLangMstDao sccSltnLangMstDao;
    
    @Autowired
    private SccUiMlangMstDao sccUiMlangMstDao;
    
    @Override
    @Transactional
    public String multilingual(String revision, String execPackage) throws Exception {
        
        Map<String, String> fileListMap = null;
        
        StringBuffer responseSB = null;
        String responseMessage = "";
        String filePath = "";
        String contents = "";
        
        int insertResult = 0;
        int deleteResult = 0;
        
        // +------------------+
        //     SVN File 조회
        // +------------------+
        if ( StringUtils.isEmpty(execPackage) ) {
            fileListMap = EspSvnUtils.getSvnFileList(Long.parseLong(revision));
        } 
        // +-------------------+
        //     Package 파일조회
        // +-------------------+
        else if ( StringUtils.isNotEmpty(execPackage) ) {
            Properties prop = EspFileUtils.getProperties("ioxml.properties");
            String propPackage = prop.getProperty(execPackage);
            fileListMap = EspFileUtils.getFileList( propPackage );
        }
        
        if ( null != fileListMap && fileListMap.size() > 0) {
            
            for (Map.Entry<String, String> entry : fileListMap.entrySet()) {
                // +-------------------------+
                //     '.xfdl' 파일의 내용 추출
                // +-------------------------+
                filePath = entry.getKey();
                if (StringUtils.isNotEmpty(execPackage)) {
                    contents = EspFileUtils.getFileContents(filePath);
                } else {
                    contents = EspSvnUtils.getSvnFileContents(filePath);
                }
                
                // +--------------------------------------------------------+
                //     데이타가 5바이트 이상이여만 파싱후 작업 히스토리때문에 삭제 되었을수도 있음.
                // +--------------------------------------------------------+
                if (contents.length() > 5) {
                    
                    /*
                    logger.info("==================================================");
                    logger.info("◆ ■ ◆ entry.getKey() : ["+ entry.getKey() + "]");
                    logger.info("==================================================");
                    */
                    
                    // +----------------------------+
                    //     '.xfdl' 파일 내용  XML 파싱       
                    // +----------------------------+
                    List<String> parseContentList = uiParser(contents);
                    
                    // +---------------+
                    //     DB 처리 시작
                    // +---------------+
                    String fileName = new File(filePath).getName().replaceAll(".xfdl".trim(), "".trim());
                    String uiSepCd  = "";
                    if ( filePath.contains("Biz".trim()) ) {
                        uiSepCd = "B";
                    }
                    else if ( filePath.contains("Main".trim()) ) {
                        uiSepCd = "C";
                    }
                    
                    // +--------------------------------------+
                    //     DB 언어 데이터와 UI 파싱 데이터를 비교하여 처리.
                    // +--------------------------------------+
                    
                    // +----------------------------------------------------+
                    //     SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) - 조회(Select)
                    // +----------------------------------------------------+
                    List<Map<String, Object>> sccUiMlangMstList = sccUiMlangMstDao.selectSccUiMlangMst(fileName);
                    
                    // +--------------------------------------------------------+
                    //     SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) 데이터가 존재하면 머지 작업
                    // +--------------------------------------------------------+
                    if (null != sccUiMlangMstList && sccUiMlangMstList.size() > 0) {
                        
                        // +----------------------------------------------------------------------+
                        //     SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) 테이블 데이터와, UI Parsing Data 비교
                        //     테이블에 존재하지 않으면 insert
                        // +----------------------------------------------------------------------+
                        for ( String parseContent : parseContentList ) {
                            int tmpCnt = 0;
                            for ( int i=0 ; i<sccUiMlangMstList.size() ; i++ ) {
                                if ( sccUiMlangMstList.get(i).get("MLANG_BASE_CNTN").toString().contains(parseContent) ) {
                                    tmpCnt++;
                                }
                            }
                            if ( tmpCnt == 0 ) {
                                List<String> tmpList = new ArrayList<String>();
                                tmpList.add(parseContent);
                                insertResult =+ processInsertSccUiMlangMst(tmpList, fileName, uiSepCd);
                            }
                        }
                        
                        // +----------------------------------------------------------------------+
                        //     SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) 테이블 데이터와, UI Parsing Data 비교
                        //     테이블의 데이터가 UI Parsing Data 에 존재하지 않으면 delete
                        // +----------------------------------------------------------------------+
                        for ( int i=0 ; i<sccUiMlangMstList.size() ; i++ ) {
                            int tmpCnt = 0;
                            String deleteMlangBaseCntn = sccUiMlangMstList.get(i).get("MLANG_BASE_CNTN").toString();
                            String deleteLangCd = sccUiMlangMstList.get(i).get("LANG_CD").toString();
                             for ( String tmpParseContent : parseContentList ) {
                                if ( deleteMlangBaseCntn.contains(tmpParseContent) ) {
                                    tmpCnt++;
                                }
                            }
                            if ( tmpCnt == 0 ) {
                                deleteResult =+ sccUiMlangMstDao.deleteSccUiMlangMst(fileName, deleteLangCd, deleteMlangBaseCntn);
                            }
                        }
                        
                        if ( insertResult == 0 && deleteResult == 0 ) {
                            responseSB = new StringBuffer();
                            responseSB.append("Multilingual data to reflect does not exist.\n");
                            responseSB.append("Multilingual already reflected.\n");
                            responseSB.append("반영할 다국어 데이터가 존재하지 않습니다.\n");
                            responseSB.append("이미 반영된 다국어 입니다.");
                            responseMessage = responseSB.toString();
                        } else {
                            responseSB = new StringBuffer();
                            responseSB.append("'").append(insertResult).append("'").append(" multilingual data saved.\n");
                            responseSB.append("다국어 데이터가 '").append(insertResult).append("'건 저장 되었습니다.\n\n");
                            responseSB.append("'").append(deleteResult).append("'").append(" multilingual data have been deleted.\n");
                            responseSB.append("다국어 데이터가 '").append(deleteResult).append("'건 삭제 되었습니다.");
                            responseMessage = responseSB.toString();
                        }
                    }
                    // +-------------------------------------------------------+
                    //     SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) 데이터가 없으면 신규 생성
                    // +-------------------------------------------------------+
                    else {
                        insertResult = processInsertSccUiMlangMst(parseContentList, fileName, uiSepCd);
                        
                        responseSB = new StringBuffer();
                        responseSB.append("'").append(insertResult).append("'").append(" new multilingual data saved.\n");
                        responseSB.append("신규 다국어 데이터가 '").append(insertResult).append("'건 저장 되었습니다.");
                        responseMessage = responseSB.toString();
                    }
                }
            }
        } else {
            responseSB = new StringBuffer();
            responseSB.append("The target file does not exist.\n");
            responseSB.append("대상 파일이 존재하지 않습니다.");
            responseMessage = responseSB.toString();
        }
        
        return responseMessage;
    }

    /**
     * Nexacro UI Parsing - 한글 기준으로 데이터 추출
     * @param String xml
     * @return ArrayList<HashMap<String, String>> uiLangList
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private List<String> uiParser(String xml) throws Exception {

        List<String> uiLangList = new ArrayList<String>();
        
        // +--------------------------+
        //     Get Document Builder
        // +--------------------------+
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        builder = factory.newDocumentBuilder();

        // +--------------------+
        //     Build Document
        // +--------------------+
        Document document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        document.getDocumentElement().normalize();

        // +---------------+
        //     xpath 생성
        // +---------------+
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression;

        // +----------------------------------------------+
        //     all elements that have the key attribute
        // +----------------------------------------------+
        expression = "//*[@text]";
        NodeList xPathNodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        
        if (null != xPathNodeList) {
            
            /*
            logger.info("===================================================");
            logger.info("    다국어 대상 : [" + xPathNodeList.getLength() + "] 건");
            logger.info("===================================================");
            */
            
            Node         xPathNode       = null;
            NamedNodeMap xPathNodeAttr   = null;
            
            String objId      = "";
            String baseLangNm = "";
            
            for (int i = 0; i < xPathNodeList.getLength(); i++) {
                
                xPathNode     = xPathNodeList.item(i);
                xPathNodeAttr = xPathNode.getAttributes();

                /*
                logger.info("--------------------------------------------------");
                logger.info("--- Parent Node Name : [" + xPathNode.getParentNode().getNodeName() + "]");
                */
                
                if (null != xPathNodeAttr.getNamedItem("id")) {
                    
                    objId      = xPathNodeAttr.getNamedItem("id").getTextContent();
                    baseLangNm = xPathNodeAttr.getNamedItem("text").getTextContent();
                }
                else if (null != xPathNodeAttr.getNamedItem("col")) {
                    
                    String parentNodeName   = xPathNode.getParentNode().getNodeName();    // 'Band'
                    String parentNodeIdText = xPathNode.getParentNode().getAttributes().getNamedItem("id").getTextContent();    // head, summary
                    
                    if ("Band".equals(parentNodeName) && "head".equals(parentNodeIdText) || "summary".equals(parentNodeIdText)) {
                        
                        /*
                        logger.info("--- Parent Node id text : [" + xPathNode.getParentNode().getAttributes().getNamedItem("id").getTextContent() + "]");
                        */
                        
                        objId = xPathNodeAttr.getNamedItem("col").getTextContent();
                        baseLangNm = xPathNodeAttr.getNamedItem("text").getTextContent();
                    } 
                }
                
                if (StringUtils.isNotEmpty(baseLangNm)) {
                    uiLangList.add(baseLangNm);
                }
                
                /*
                logger.info("--- Node Name : [" + xPathNode.getNodeName() + "]");
                logger.info("--- Attribute : [" + objId + "] / ["+ baseLangNm + "]");
                logger.info("--------------------------------------------------");
                */
            }
            
        }

        // +--------------------+
        //     tooltiptext 추가
        // <Button id="btn_personsave" taborder="13" width="40" height="32" right="39" cssclass="btn_personalsave" onclick="btn_common_onclick" tabstop="false" bottom="5" tooltiptext="personal save"/>
        // +--------------------+
        expression = "//*[@tooltiptext]";
        xPathNodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        if (null != xPathNodeList) {
            Node         xPathNode       = null;
            NamedNodeMap xPathNodeAttr   = null;
            String objId      = "";
            String baseLangNm = "";
            for (int i = 0; i < xPathNodeList.getLength(); i++) {
                xPathNode     = xPathNodeList.item(i);
                xPathNodeAttr = xPathNode.getAttributes();
                if ( null != xPathNodeAttr.getNamedItem("tooltiptext") ) {
                    baseLangNm = xPathNodeAttr.getNamedItem("tooltiptext").getTextContent();
                }
                if ( StringUtils.isNotEmpty(baseLangNm) ) {
                    uiLangList.add(baseLangNm);
                }
            }
        }
        
        // 중복제거
        List<String> newUiLangList = new ArrayList<String>();
        for (int i = 0; i < uiLangList.size(); i++) {
            if (!newUiLangList.contains(uiLangList.get(i))) {
                newUiLangList.add(uiLangList.get(i));
            }
        }
        
        logger.info("==================================================");
        logger.info("[uiLangList]");
        logger.info("--------------------------------------------------");
        logger.info(uiLangList.toString());
        logger.info("--------------------------------------------------");
        logger.info(newUiLangList.toString());
        logger.info("==================================================");

        return newUiLangList;
    }
    
    /**
     * <pre>
     * SCC 솔루션 언어 마스터(SCC_SLTN_LANG_MST) 테이블에 등록된 다국어를 기준으로 
     * 화면에서 파싱된 데이터를 
     * SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) 테이블에 Insert 한다.
     * </pre>
     * @param parseContentList 파싱된 UI 데이터
     * @param fileName 파일명(프로그램 ID)
     * @param uiSepCd 화면 구분 코드
     * @throws Exception
     */
    private int processInsertSccUiMlangMst (List<String> parseContentList, String fileName, String uiSepCd) throws Exception {
        
        int insertResult = 0;
        
        String LANG_CD = "";
        
        // +-----------------------------------------------------+
        //     SCC 솔루션 언어 마스터(SCC_SLTN_LANG_MST) - 조회(Select)
        //     SCC_SLTN_LANG_MST 테이블에서 조회된 다국어 수 만큼 
        //     SCC_UI_MLANG_MST  테이블에 Insert
        //     예를들어 솔루션에서 사용할 언어가 한글, 영어, 중문, 일어 인 경우
        //     다국어 마스터 테이블에 '조회' 라는 데이터가 언어별로 Insert 되어야 한다.
        //     '조회'데이터는 언어별로 한건씩 총 4건이 등록된다.
        // +-----------------------------------------------------+
        
        List<Map<String, Object>> sccSltnLangMstList = sccSltnLangMstDao.selectSccSltnLangMst("1");
        
        if (null != sccSltnLangMstList && sccSltnLangMstList.size() > 0) {
            
            for (int i=0 ; i<sccSltnLangMstList.size() ; i++) {
                LANG_CD = sccSltnLangMstList.get(i).get("LANG_CD").toString();
                for (String MLANG_BASE_CNTN : parseContentList) {
                    insertResult =+ sccUiMlangMstDao.insertSccUiMlangMst(fileName, LANG_CD, MLANG_BASE_CNTN, uiSepCd, MLANG_BASE_CNTN);
                    logger.info("=================================================");
                    logger.info("    파일명(프로그램 ID) : [" + fileName + ".xfdl]");
                    logger.info("    언어코드 : [" + LANG_CD + "] 저장");
                    logger.info("    다국어 기본 내용 : [" + MLANG_BASE_CNTN + "] 저장");
                    logger.info("    화면 구분 코드 : [" + uiSepCd + "] 저장");
                    logger.info("    다국어 내용 : [" + MLANG_BASE_CNTN + "] 저장");
                    logger.info("=================================================");
                }
            }
        }
        
        return insertResult;
    }
}

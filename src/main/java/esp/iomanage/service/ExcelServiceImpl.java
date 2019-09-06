package esp.iomanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import esp.iomanage.dao.SccUiFuncMstDao;

@Service
public class ExcelServiceImpl implements ExcelService {

    private Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private SccUiFuncMstDao sccUiFuncMstDao;
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void uiScriptParser(String jsonString) throws Exception {

        /*
        logger.info("==================================================");
        logger.info(jsonString);
        logger.info("==================================================");
        */
        
        // +-----------------------+
        //     Json to Map, List
        // +-----------------------+
        Gson gson = new Gson();
                
        JsonParser  jsonParser  = new JsonParser();        
        JsonElement jsonElement = jsonParser.parse(jsonString);

        JsonObject headerJsonObject = jsonElement.getAsJsonObject().get("header")  .getAsJsonObject();
        JsonArray functionJsonArray = jsonElement.getAsJsonObject().get("function").getAsJsonArray();
        JsonArray objectJsonArray   = jsonElement.getAsJsonObject().get("object")  .getAsJsonArray();
        
        Map<String, String>       headerMap    = (Map<String, String>)       gson.fromJson(headerJsonObject,  HashMap.class);
        List<Map<String, String>> functionList = (List<Map<String, String>>) gson.fromJson(functionJsonArray, List.class);
        List<Map<String, String>> objectList   = (List<Map<String, String>>) gson.fromJson(objectJsonArray,   List.class);
        
        /*
        logger.info("==================================================");
        logger.info("" + headerMap);
        logger.info("--------------------------------------------------");
        logger.info("" + functionList);
        logger.info("--------------------------------------------------");
        logger.info("" + objectList);
        logger.info("==================================================");
        */
        
        String prgmId          = headerMap.get("program_id");    // 1. 프로그램 ID
        int    funcSerl        = 1;     // 2. 기능 순번
        String svcId           = "";    // 3. 서비스ID
        String uiId            = "";    // 4. 화면 ID
        String intmdVriablCntn = "";    // 5. 매개 변수 내용
        String funcNm          = "";    // 6. 기능 명
        String funcDescr       = "";    // 7. 기능 설명    
        String rsltCntn        = "";    // 8. 결과 내용
        String rmk             = "";    // 9. 비고
        
        int deleteCnt = 0;
        int insertCnt = 0;
        
        Map<String, Object> queryParamMap = new HashMap<String, Object>();
        if ( StringUtils.isNotEmpty(prgmId) ) {
            
            // +----------------------------------------------------+
            //     [Delete] SCC 화면 기능 마스터 - SCC_UI_FUNC_MST Table
            // +----------------------------------------------------+
            deleteCnt = sccUiFuncMstDao.deleteSccUiFuncMst(prgmId);
            
            logger.info("======================================================");
            logger.info("SCC_UI_FUNC_MST Delete Result : [" + deleteCnt + "] 건");
            logger.info("======================================================");
            
            for (Map<String, String> functionMap : functionList) {
                // +------------------------------------------------------------------------------------+
                //     Script Function Name 에 해당하는 Function 내부의 objArgument, objPopupData 항목을 셋팅한다. 
                // +------------------------------------------------------------------------------------+
                funcNm    = functionMap.get("function_name");
                funcDescr = functionMap.get("description");
                Map<String, String> findObjectInfoMap = findObjectInfo(funcNm, objectList);
                String funcType = findObjectInfoMap.get("function_type");
                if ( StringUtils.isNotEmpty( funcType ) ) {
                    if ( "normal".equals(funcType) ) {
                        svcId           = findObjectInfoMap.get("scontroller");
                        intmdVriablCntn = findObjectInfoMap.get("inds");
                        rsltCntn        = findObjectInfoMap.get("outds");
                    }
                    else if ( "popup".equals(funcType) ) {
                        uiId            = findObjectInfoMap.get("formurl");
                        intmdVriablCntn = findObjectInfoMap.get("objArgument");
                        rsltCntn        = functionMap.get("return");
                    } else {
                        intmdVriablCntn = functionMap.get("param");
                        rsltCntn        = functionMap.get("return");
                    }
                } else {
                    intmdVriablCntn = functionMap.get("param");
                    rsltCntn        = functionMap.get("return");
                }
                // +----------------------------------------------------+
                //     [Insert] SCC 화면 기능 마스터 - SCC_UI_FUNC_MST Table
                // +----------------------------------------------------+
                queryParamMap.put("prgmId", prgmId);
                queryParamMap.put("funcSerl", funcSerl);
                queryParamMap.put("svcId", svcId);
                queryParamMap.put("uiId", uiId);
                queryParamMap.put("intmdVriablCntn", intmdVriablCntn);
                queryParamMap.put("funcNm", funcNm);
                queryParamMap.put("funcDescr", funcDescr);
                queryParamMap.put("rsltCntn", rsltCntn);
                queryParamMap.put("rmk", rmk);
                /*
                logger.info("==================================================");
                logger.info(":: findObjectInfoMap ::\n" + findObjectInfoMap);
                logger.info(":: queryParamMap ::\n" + queryParamMap);
                logger.info("==================================================");
                */
                insertCnt += sccUiFuncMstDao.insertSccUiFuncMst(queryParamMap);
                
                // +-------------+
                //     변수 초기화
                // +-------------+
                svcId           = "";
                uiId            = "";
                intmdVriablCntn = "";
                funcNm          = "";
                funcDescr       = "";
                rsltCntn        = "";
                rmk             = "";
                queryParamMap = new HashMap<String, Object>();
                
                funcSerl++;
            }
            logger.info("======================================================");
            logger.info("SCC_UI_FUNC_MST Insert Result : [" + insertCnt + "] 건");
            logger.info("======================================================");
        }
    }
    
    /**
     * Script Function Name 에 해당하는 Function 내부의 objArgument, objPopupData 항목을 셋팅한다.  
     * @param functionName
     * @param scriptFunctionObjectList
     * @throws Exception
     */
    private Map<String, String> findObjectInfo(String functionName, List<Map<String, String>> scriptFunctionObjectList) throws Exception {
        Map<String, String> resultMap = new HashMap<String, String>();
        for ( Map<String, String> tmpMap : scriptFunctionObjectList) {
            // +--------------------------------+
            //     Function Name 이 같으면 추출대상.
            // +--------------------------------+
            if ( StringUtils.isNotEmpty(tmpMap.get("function_name")) && functionName.equals(tmpMap.get("function_name"))) {
                /*
                logger.info("==================================================");
                logger.info("tmpMap : \n" + tmpMap);
                logger.info("==================================================");
                */
                if ( null == resultMap.get("function_name") || StringUtils.isEmpty(resultMap.get("function_name")) ) {
                    if ( null != tmpMap.get("function_name") && StringUtils.isNotEmpty(tmpMap.get("function_name")) ) {
                        resultMap.put("function_name", tmpMap.get("function_name"));
                    }
                }
                if ( null == resultMap.get("description") || StringUtils.isEmpty(resultMap.get("description")) ) {
                    if ( null != tmpMap.get("description") && StringUtils.isNotEmpty(tmpMap.get("description")) ) {
                        resultMap.put("description", tmpMap.get("description"));
                    }
                }
                if ( null == resultMap.get("function_type") || StringUtils.isEmpty(resultMap.get("function_type")) ) {
                    if ( null != tmpMap.get("function_type") && StringUtils.isNotEmpty(tmpMap.get("function_type")) ) {
                        resultMap.put("function_type", tmpMap.get("function_type"));
                    }
                }
                if ( null == resultMap.get("svcid") || StringUtils.isEmpty(resultMap.get("svcid")) ) {
                    if ( null != tmpMap.get("svcid") && StringUtils.isNotEmpty(tmpMap.get("svcid")) ) {
                        resultMap.put("svcid", tmpMap.get("svcid"));
                    }
                }
                if ( null == resultMap.get("scontroller") || StringUtils.isEmpty(resultMap.get("scontroller")) ) {
                    if ( null != tmpMap.get("scontroller") && StringUtils.isNotEmpty(tmpMap.get("scontroller")) ) {
                        resultMap.put("scontroller", tmpMap.get("scontroller"));
                    }
                }
                if ( null == resultMap.get("inds") || StringUtils.isEmpty(resultMap.get("inds")) ) {
                    if ( null != tmpMap.get("inds") && StringUtils.isNotEmpty(tmpMap.get("inds")) ) {
                        resultMap.put("inds", tmpMap.get("inds"));
                    }
                }
                if ( null == resultMap.get("outds") || StringUtils.isEmpty(resultMap.get("outds")) ) {
                    if ( null != tmpMap.get("outds") && StringUtils.isNotEmpty(tmpMap.get("outds")) ) {
                        resultMap.put("outds", tmpMap.get("outds"));
                    }
                }
                if ( null == resultMap.get("objArgument") || StringUtils.isEmpty(resultMap.get("objArgument")) ) {
                    if ( null != tmpMap.get("objArgument") && StringUtils.isNotEmpty(tmpMap.get("objArgument")) ) {
                        resultMap.put("objArgument", tmpMap.get("objArgument"));
                    }
                }
                if ( null == resultMap.get("popupid") || StringUtils.isEmpty(resultMap.get("popupid")) ) {
                    if ( null != tmpMap.get("popupid") && StringUtils.isNotEmpty(tmpMap.get("popupid")) ) {
                        resultMap.put("popupid", tmpMap.get("popupid"));
                    }
                }
                if ( null == resultMap.get("formurl") || StringUtils.isEmpty(resultMap.get("formurl")) ) {
                    if ( null != tmpMap.get("formurl") && StringUtils.isNotEmpty(tmpMap.get("formurl")) ) {
                        resultMap.put("formurl", tmpMap.get("formurl"));
                    }
                }
                if ( null == resultMap.get("oargs") || StringUtils.isEmpty(resultMap.get("oargs")) ) {
                    if ( null != tmpMap.get("oargs") && StringUtils.isNotEmpty(tmpMap.get("oargs")) ) {
                        resultMap.put("oargs", tmpMap.get("oargs"));
                    }
                }
            }
        }// end for
        /*
        logger.info("==================================================");
        logger.info("[resultMap]\n" + resultMap);
        logger.info("==================================================");
        */
        return resultMap;
    }
}

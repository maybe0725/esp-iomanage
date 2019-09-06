package esp.iomanage.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SccUiMlangMstDaoImpl implements SccUiMlangMstDao {

    private Logger logger = LoggerFactory.getLogger(SccUiMlangMstDaoImpl.class);
    
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }
    
    @Override
    public List<Map<String, Object>> selectSccUiMlangMst(String prgmId) throws Exception {
        
        StringBuffer sb = new StringBuffer();
        sb.append("  SELECT                 ");
        sb.append("       PRGM_ID           "); // 프로그램 ID
        sb.append("      ,LANG_CD           "); // 언어 코드
        sb.append("      ,MLANG_BASE_CNTN   "); // 다국어 기본 내용
        sb.append("      ,UI_SEP_CD         "); // 화면 구분 코드 (B:Biz/C:Common)
        sb.append("      ,MLANG_CNTN        "); // 다국어 내용
        sb.append("      ,RMK               "); // 비고
        sb.append("      ,USE_YN            "); // 사용여부
        sb.append("      ,DEL_YN            "); // 삭제여부
        sb.append("  FROM SCC_UI_MLANG_MST  ");
        sb.append("  WHERE 1=1              ");
        sb.append("    AND PRGM_ID = ?      ");
        
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sb.toString(), prgmId);
        
        /*
        logger.info("==================================================");
        for (Map<String, Object> tmpMap : result) {
            logger.info("PRGM_ID         : [" + tmpMap.get("PRGM_ID")         + "]");
            logger.info("LANG_CD         : [" + tmpMap.get("LANG_CD")         + "]");
            logger.info("MLANG_BASE_CNTN : [" + tmpMap.get("MLANG_BASE_CNTN") + "]");
            logger.info("UI_SEP_CD       : [" + tmpMap.get("UI_SEP_CD")       + "]");
            logger.info("MLANG_CNTN      : [" + tmpMap.get("MLANG_CNTN")      + "]");
            logger.info("RMK             : [" + tmpMap.get("RMK")             + "]");
            logger.info("USE_YN          : [" + tmpMap.get("USE_YN")          + "]");
            logger.info("DEL_YN          : [" + tmpMap.get("DEL_YN")          + "]");
            logger.info("==================================================");
        }
        */
        
        return result;
    }
    
    @Override
    public int insertSccUiMlangMst(String prgmId, String langCd, String mlangBaseCntn, String uiSepCd, String mlangCntn) throws Exception {

        logger.info("==================================================");
        logger.info(">>> [START] SccUiMlangMstDaoImpl > insertSccUiMlangMst");
        logger.info("--------------------------------------------------");
        logger.info("  prgmId        : [" + prgmId + "]");
        logger.info("  langCd        : [" + langCd + "]");
        logger.info("  mlangBaseCntn : [" + mlangBaseCntn + "]");
        logger.info("  uiSepCd       : [" + uiSepCd + "]");
        logger.info("  mlangCntn     : [" + mlangCntn + "]");
        logger.info("==================================================");
        
        StringBuffer sb = new StringBuffer();
        sb.append("  INSERT INTO SCC_UI_MLANG_MST(  ");
        sb.append("       PRGM_ID                   "); // 프로그램 ID   
        sb.append("      ,LANG_CD                   "); // 언어 코드     
        sb.append("      ,MLANG_BASE_CNTN           "); // 다국어 기본 내용 
        sb.append("      ,UI_SEP_CD                 "); // 화면 구분 코드 (B:Biz/C:Common)
        sb.append("      ,MLANG_CNTN                "); // 다국어 내용    
        sb.append("      ,USE_YN                    "); // 사용 여부     
        sb.append("      ,DEL_YN                    "); // 삭제 여부     
        sb.append("      ,CRATR_ID                  "); // 생성자 ID (SYSTEM)
        sb.append("      ,CRAT_DT                   "); // 생성 일시     
        sb.append("      ,CRAT_SYS_DT               "); // 생성 시스템 일시 
        sb.append("      ,UPDT_DT                   "); // 수정 일시
        sb.append("      ,UPDT_SYS_DT               "); // 수정 시스템 일시
        sb.append("  )VALUES(                       ");
        sb.append("       ?                         "); 
        sb.append("      ,?                         ");
        sb.append("      ,?                         "); 
        sb.append("      ,?                         "); 
        sb.append("      ,?                         "); 
        sb.append("      ,'1'                       "); 
        sb.append("      ,'0'                       "); 
        sb.append("      ,'SYSTEM'                  ");
        sb.append("      ,SYSDATE                   "); 
        sb.append("      ,SYSDATE                   ");
        sb.append("      ,SYSDATE                   "); 
        sb.append("      ,SYSDATE                   ");
        sb.append("  )");
        
        int insertResult = jdbcTemplate.update(sb.toString(), new Object[] {prgmId, langCd, mlangBaseCntn, uiSepCd, mlangCntn});
        
        return insertResult;
    }
    
    @Override
    public int deleteSccUiMlangMst(String prgmId, String langCd, String mlangBaseCntn) throws Exception {
        
        StringBuffer sb = new StringBuffer();
        sb.append("     DELETE FROM SCC_UI_MLANG_MST  ");
        sb.append("      WHERE 1=1                    ");
        sb.append("        AND PRGM_ID         = ?    ");
        sb.append("        AND LANG_CD         = ?    ");
        sb.append("        AND MLANG_BASE_CNTN = ?    ");
        
        int deleteResult = jdbcTemplate.update(sb.toString(), new Object[] {prgmId, langCd, mlangBaseCntn});
        
        return deleteResult;
    }
}

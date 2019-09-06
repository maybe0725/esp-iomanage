package esp.iomanage.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SccUiFuncMstDaoImpl implements SccUiFuncMstDao {

    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }
    
    @Override
    public int deleteSccUiFuncMst(String prgmId) throws Exception {
        
        StringBuffer deleteQuerySB = new StringBuffer();
        deleteQuerySB.append("  DELETE FROM SCC_UI_FUNC_MST  ");
        deleteQuerySB.append("  WHERE PRGM_ID = ?            ");

        int deleteResult = jdbcTemplate.update(deleteQuerySB.toString(), prgmId);
        
        return deleteResult;
    }
    
    @Override
    public int insertSccUiFuncMst(Map<String, Object> queryParamMap) throws Exception {
        
        StringBuffer insertQuerySB = new StringBuffer();
        insertQuerySB.append("  INSERT INTO SCC_UI_FUNC_MST (  ");
        insertQuerySB.append("        PRGM_ID                  ");
        insertQuerySB.append("      , FUNC_SERL                ");
        insertQuerySB.append("      , SVC_ID                   ");
        insertQuerySB.append("      , UI_ID                    ");
        insertQuerySB.append("      , INTMD_VRIABL_CNTN        ");
        insertQuerySB.append("      , FUNC_NM                  ");
        insertQuerySB.append("      , FUNC_DESCR               ");
        insertQuerySB.append("      , RSLT_CNTN                ");
        insertQuerySB.append("      , RMK                      ");
        insertQuerySB.append("      , CRATR_ID                 ");
        insertQuerySB.append("      , CRAT_DT                  ");
        insertQuerySB.append("      , CRAT_SYS_DT              ");
        insertQuerySB.append("      , UPDTR_ID                 ");
        insertQuerySB.append("      , UPDT_DT                  ");
        insertQuerySB.append("      , UPDT_SYS_DT              ");
        insertQuerySB.append("  ) VALUES (                     ");
        insertQuerySB.append("        ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , ?                        ");
        insertQuerySB.append("      , 'SYSTEM'                 ");
        insertQuerySB.append("      , SYSDATE                  ");
        insertQuerySB.append("      , SYSDATE                  ");
        insertQuerySB.append("      , 'SYSTEM'                 ");
        insertQuerySB.append("      , SYSDATE                  ");
        insertQuerySB.append("      , SYSDATE                  ");
        insertQuerySB.append("  )                              ");
        
        String prgmId          = "";         
        int    funcSerl        = 0;
        String svcId           = "";
        String uiId            = "";
        String intmdVriablCntn = "";
        String funcNm          = "";
        String funcDescr       = "";
        String rsltCntn        = "";
        String rmk             = "";
        
        if ( null != queryParamMap ) {
            prgmId          = (String) queryParamMap.get("prgmId"); 
            funcSerl        = (int)    queryParamMap.get("funcSerl");
            svcId           = (String) queryParamMap.get("svcId");
            uiId            = (String) queryParamMap.get("uiId");
            intmdVriablCntn = (String) queryParamMap.get("intmdVriablCntn");
            funcNm          = (String) queryParamMap.get("funcNm");
            funcDescr       = (String) queryParamMap.get("funcDescr");
            rsltCntn        = (String) queryParamMap.get("rsltCntn");
            rmk             = (String) queryParamMap.get("rmk");
        }
        
        int insertResult = jdbcTemplate.update(
                    insertQuerySB.toString(), 
                    new Object[] {
                        prgmId,         
                        funcSerl,       
                        svcId,          
                        uiId,           
                        intmdVriablCntn,
                        funcNm,         
                        funcDescr,      
                        rsltCntn,       
                        rmk            
                    }
                );
        
        return insertResult;
    }
    
}

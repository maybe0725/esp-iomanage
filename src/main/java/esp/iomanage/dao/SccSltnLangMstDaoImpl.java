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
public class SccSltnLangMstDaoImpl implements SccSltnLangMstDao {

    private Logger logger = LoggerFactory.getLogger(SccSltnLangMstDaoImpl.class);
    
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }
    
    @Override
    public List<Map<String, Object>> selectSccSltnLangMst(String useYn) throws Exception {
        logger.info("==================================================");
        logger.info(">>> [START] SccSltnLangMstDAO > selectSccSltnLangMst");
        logger.info("==================================================");
        
        StringBuffer sb = new StringBuffer();
        sb.append("  SELECT                  ");
        sb.append("       SLTN_ID            ");    // 솔루션 ID
        sb.append("      ,LANG_CD            ");    // 언어코드
        sb.append("      ,LANG_CD_NM         ");    // 언어 코드 명
        sb.append("      ,USE_YN             ");    // 사용여부
        sb.append("  FROM SCC_SLTN_LANG_MST  "); 
        sb.append("  WHERE 1=1               ");
        sb.append("    AND USE_YN = ?        ");
        
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sb.toString(), useYn);
        
        logger.info("==================================================");
        for (Map<String, Object> tmpMap : resultList) {
            logger.info("SLTN_ID : [" + tmpMap.get("SLTN_ID") + "]");
            logger.info("LANG_CD : [" + tmpMap.get("LANG_CD") + "]");
            logger.info("LANG_CD_NM : [" + tmpMap.get("LANG_CD_NM") + "]");
            logger.info("USE_YN : [" + tmpMap.get("USE_YN") + "]");
            logger.info("==================================================");
        }
        
        return resultList;
    }
}

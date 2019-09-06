package esp.iomanage.dao;

import java.util.Map;

public interface SccUiFuncMstDao {
    
    /**
     * [Delete] SCC 화면 기능 마스터 - SCC_UI_FUNC_MST Table
     * @param prgmId
     * @return Delete Count
     * @throws Exception
     */
    public int deleteSccUiFuncMst(String prgmId) throws Exception;
    
    /**
     * [Insert] SCC 화면 기능 마스터 - SCC_UI_FUNC_MST Table
     * @param queryParamMap
     * @return Insert Count
     * @throws Exception
     */
    public int insertSccUiFuncMst(Map<String, Object> queryParamMap) throws Exception;
    
}

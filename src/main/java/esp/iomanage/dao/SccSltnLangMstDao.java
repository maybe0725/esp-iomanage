package esp.iomanage.dao;

import java.util.List;
import java.util.Map;

public interface SccSltnLangMstDao {

    /**
     * SCC 솔루션 언어 마스터(SCC_SLTN_LANG_MST) - 조회(Select)
     * @param useYn - '0' : 미사용 / '1' : 사용 
     * @throws Exception
     */
    public List<Map<String, Object>> selectSccSltnLangMst(String useYn) throws Exception;
}

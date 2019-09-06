package esp.iomanage.dao;

import java.util.List;
import java.util.Map;

public interface SccUiMlangMstDao {

    /**
     * SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) - 조회(Select)
     * @param prgmId 프로그램 ID
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> selectSccUiMlangMst(String prgmId) throws Exception;
    /**
     * SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) - 저장(Insert)
     * @param prgmId 프로그램 ID
     * @param langCd 언어 코드
     * @param mlangBaseCntn 다국어 기본 내용
     * @param mlangCntn 다국어 내용
     * @param uiSepCd 화면 구분 코드
     * @return
     * @throws Exception
     */
    public int insertSccUiMlangMst(String prgmId, String langCd, String mlangBaseCntn, String mlangCntn, String uiSepCd) throws Exception;
    /**
     * SCC 화면 다국어 마스터(SCC_UI_MLANG_MST) - 삭제(Delete)
     * @param prgmId 프로그램 ID
     * @param langCd 언어 코드
     * @param mlangBaseCntn 다국어 기본 내용
     * @return
     * @throws Exception
     */
    public int deleteSccUiMlangMst(String prgmId, String langCd, String mlangBaseCntn) throws Exception;
}

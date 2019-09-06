package esp.iomanage.utils;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class EspFileUtils {

//    private static Logger logger = LoggerFactory.getLogger(EspFileUtils.class);
    
    /**
     * .properties 파일을 classpath 에서 읽어온다.
     * @return
     * @throws Exception
     */
    public static Properties getProperties(String fileName) throws Exception {
        
        Properties prop = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        prop.load(new InputStreamReader(cl.getResourceAsStream(fileName), "UTF-8"));

        return prop;
    }
    
    /**
     * Task Package 에 존재하는 파일 리스트를 조회 후 리턴한다.
     * @param tackPackage
     * @return
     * @throws Exception
     */
    public static Map<String, String> getFileList(String tackPackage) throws Exception {
        Map<String, String> fileListMap = new HashMap<String, String>();
        for (File file : FileUtils.listFiles(new File(tackPackage), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            fileListMap.put(file.getPath(), file.getPath());
        }
        return fileListMap;
    }
    
    /**
     * 파일내용 추출
     * @param filePath
     * @return String contents
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static String getFileContents(String filePath) throws Exception {
        
        File file = new File(filePath);
        Scanner scan = new Scanner(file);
        
        StringBuffer sb = new StringBuffer();
                
        while (scan.hasNextLine()) {
            sb.append(scan.nextLine());
        }
        String contents = sb.toString();
        
        return contents.toString();
    }
}

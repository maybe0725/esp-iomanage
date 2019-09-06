package esp.iomanage.utils;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class EspSvnUtils {

    private static Logger logger = LoggerFactory.getLogger(EspSvnUtils.class);
    
    /**
     * currentRevsionNumber 에 해당하는 SVN 파일 가져오기
     * @param currentRevsionNumber
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    public static Map<String, String> getSvnFileList(long currentRevsionNumber) throws Exception {

        Map<String, String> filePathList = new HashMap<String, String>();

        try {
        
            // +--------------------------------+
            //     설정파일 셋팅 - ioxml.properties
            // +--------------------------------+    
            Properties props = EspFileUtils.getProperties("ioxml.properties");
            String svnURL = props.getProperty("svnURL");
                   svnURL = svnURL.replaceAll("/src/main", "".trim());
            String svnId  = props.getProperty("svnId");
            String svnPwd = props.getProperty("svnPwd");
            
            logger.info("==========================================");
            logger.info("◆ ■ ◆ 설정파일 셋팅 - ioxml.properties ◆ ■ ◆");
            logger.info("------------------------------------------");
            logger.info("    svnURL : [" + svnURL + "]");
            logger.info("    svnId  : [" + svnId + "]");
            logger.info("    svnPwd : [" + svnPwd + "]");
            logger.info("==========================================");
            
             //SVNURL url =
    //         SVNURL.parseURIEncoded("http://172.25.251.115:10080/svn/IRIS/trunk/HWHNRFO");
            SVNURL.parseURIEncoded("svn://172.16.51.72/esp_development/esp-online-web");
            SVNURL url = SVNURL.parseURIEncoded(svnURL);
            String userName = svnId;
            String userPwd  = svnPwd;
            
            SVNRepository repository = SVNRepositoryFactory.create(url);
            
//            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPwd);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPwd.toCharArray());
            repository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = repository.checkPath("", -1);
            
            long lastNumber = repository.getLatestRevision();
         
            if (nodeKind == SVNNodeKind.NONE) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL ''{0}''", url);
                throw new SVNException(err);
            } else if (nodeKind == SVNNodeKind.FILE) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "Entry at URL ''{0}'' is a file while directory was expected", url);
                throw new SVNException(err);
            }
        
            Collection logEntries = null;
        
            //lastNumber
            logEntries = repository.log( new String[] { "" } , null , currentRevsionNumber-1 , currentRevsionNumber , true , true );
            //logEntries = repository.log( new String[] { "" } , null , CurrentRevsionNumber-1 , lastNumber , true , true );
            
            logger.info("\n★★★ logEntries.size() : [" + logEntries.size() +"] ★★★\n");
            
            for ( Iterator entries = logEntries.iterator( ); entries.hasNext( );) {
                SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
                
                logger.info("==================================================");
                logger.info("◆ ■ ◆ SVNLogEntry Info / Total File Count ◆ ■ ◆");
                logger.info("--------------------------------------------------");
                logger.info("svn:revision=" + logEntry.toString() );
                logger.info("==================================================");
                
                if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
                    /*
                    logger.info( "changed paths:" );
                    */
                    Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );
        
                    for ( Iterator changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
        
                        SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths().get( changedPaths.next() );
        
                        String svnPath = entryPath.getPath();
                        String fileExtension ="";
        
                        if ( svnPath.length() >= 5 ) {
                            fileExtension = svnPath.substring(svnPath.length()-5, svnPath.length());
                        }
        
                        // +-----------------------------------+
                        //     Path 가 'Biz', 'Main' 인 경우만 대상
                        // +-----------------------------------+
                        if (svnPath.contains("Biz") || svnPath.contains("Main")) {
                            // +---------------------------------+
                            //     파일명의 확장자가 '.xfdl' 인 경우만 대상
                            // +---------------------------------+
                            if (fileExtension.contains(".xfdl")) {
                                filePathList.put(entryPath.getPath(),entryPath.getPath( ));
                            }
                        }
                        
                        /*
                        logger.info("==================================================");
                        logger.info("◆ ■ ◆ SVNLogEntryPath Info ◆ ■ ◆");
                        logger.info("--------------------------------------------------");
                        logger.info("    myType         : [" + entryPath.getType()         + "]");
                        logger.info("    myPath         : [" + entryPath.getPath()         + "]");
                        logger.info("    myCopyPath     : [" + entryPath.getCopyPath()     + "]");
                        logger.info("    myCopyRevision : [" + entryPath.getCopyRevision() + "]");
                        logger.info("    myNodeKind     : [" + entryPath.getKind()         + "]");
                        logger.info("    fileExtension  : [" + fileExtension               + "]");
                        logger.info("==================================================");
                        */
                    }
                }
            }
        
            repository.closeSession();
        } catch (SVNException e) {
            e.printStackTrace();
            throw e;
        }
        
        logger.info("================================");
        logger.info("◆ ■ ◆ filePathList Info ◆ ■ ◆");
        logger.info(">>> filePathList.size() / '*.xfdl' file count : [" + filePathList.size() + "]");
        logger.info("--------------------------------");
        logger.info(filePathList.toString().replaceAll(",", "\n"));
        logger.info("================================");
        
        return filePathList;
    }
    
    /**
     * '.xfdl' 파일의 내용 추출
     * @param String filePath
     * @return String file contents
     * @throws Exception
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    public static String getSvnFileContents(String filePath) throws Exception {
        
        String result = "";

        try {
            // +--------------------------------+
            //     설정파일 셋팅 - ioxml.properties
            // +--------------------------------+  
            Properties props = EspFileUtils.getProperties("ioxml.properties");
            String svnURL = props.getProperty("svnURL");
            svnURL = svnURL.replaceAll("/src/main", "");

            String svnId  = props.getProperty("svnId");
            String svnPwd = props.getProperty("svnPwd");

            SVNURL url = SVNURL.parseURIEncoded(svnURL);
            String userName     = svnId;
            String userPassword = svnPwd;
            SVNRepository repository = SVNRepositoryFactory.create(url);

//            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword.toCharArray());
            repository.setAuthenticationManager(authManager);

            SVNProperties fileProperties = new SVNProperties();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

            if (nodeKind == SVNNodeKind.NONE) {
                logger.info("There is no entry at '" + url + "'.");
            } else if (nodeKind == SVNNodeKind.DIR) {
                logger.info("\n★★★ The entry at '" + url + "' is a directory while a file was expected. ★★★\n");
            }

            /*
             * Gets the contents and properties of the file located at filePath
             * in the repository at the latest revision (which is meant by a
             * negative revision number).
             */
            repository.getFile(filePath, -1, fileProperties, baos);

            String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);

            boolean isTextType = SVNProperty.isTextMimeType(mimeType);

            Iterator iterator = fileProperties.nameSet().iterator();

            // +-------------------------------+
            //     파일의 contents 를 담아서 리턴한다.
            // +-------------------------------+
            result = new String(baos.toByteArray(), "UTF-8");

            repository.closeSession();
        } catch (SVNException e) {

            if (e.getErrorMessage().isErrorCodeShouldShown()) {
                String svnErroCode = e.getErrorMessage().getErrorCode().toString();
                if (svnErroCode.indexOf("160013") > -1) {
                    logger.info("svn history 에는 있으나 가장 최신버전에서는 삭제되어 있습니다. 필요하시면 history 를 사용해 복구하십시요");
                } else {
                    throw e;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }
    
}

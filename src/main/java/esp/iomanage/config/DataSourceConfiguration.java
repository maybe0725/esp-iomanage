package esp.iomanage.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {
 
    private Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);
    
    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;
 
    @Value("${spring.datasource.url}")
    private String dbURL;
 
    @Value("${spring.datasource.username}")
    private String userName;
 
    @Value("${spring.datasource.password}")
    private String password;
 
    @Bean(name = "escrowMasterDataSource", destroyMethod = "postDeregister")
    public DataSource dataSource() {
        
        BasicDataSource dataSource = new BasicDataSource();
        
        logger.info("==================================================");
        logger.info("[DataSource Setting]");
        logger.info("--------------------------------------------------");
        logger.info("  dbDriverClassName : [" + dbDriverClassName + "]");
        logger.info("  dbURL : [" + dbURL + "]");
        logger.info("  userName : [" + userName + "]");
        logger.info("  password : [" + password + "]");
        logger.info("==================================================");
        
        dataSource.setDriverClassName(dbDriverClassName);
        dataSource.setUrl(dbURL);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
 
        logger.info(dataSource.toString());
        logger.info("==================================================");
        
        return dataSource;
    }
}


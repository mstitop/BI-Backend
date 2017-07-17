package cn.edu.dbsi.dataetl.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by Skye on 2017/7/11.
 */

@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"cn.edu.dbsi",})
public class HiveConnInfo {

    @Value("HIVE_USER")
    private String username;
    @Value("HIVE_PASSWD")
    private String password;
    @Value("HIVE_DRIVER_CLASS")
    private String driver;
    @Value("HIVE_DATABASE_URL")
    private String url;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

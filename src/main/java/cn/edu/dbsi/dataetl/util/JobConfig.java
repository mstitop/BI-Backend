package cn.edu.dbsi.dataetl.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Skye on 2017/7/18.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan(basePackages = {"cn.edu.dbsi",})

public class JobConfig {
    @Value("${target.hdfs.defaultFS}")
    private String defaultFS;

    @Value("${target.hdfs.fieldDelimiter}")
    private String fieldDelimiter;

    @Value("${target.hdfs.hiveAddress}")
    private String path;

    @Value("${target.hdfs.writeMode}")
    private String writeMode;

    @Value("${migration.datax.tool.folder}")
    private String dataxFloder;

    @Value("${HIVE_USER}")
    private String username;

    @Value("${HIVE_PASSWD}")
    private String password;

    @Value("${HIVE_DRIVER_CLASS}")
    private String driver;

    @Value("${HIVE_DATABASE_URL}")
    private String url;

    @Value("${KYLIN_OWNER}")
    private String kylinOwner;

    @Value("${KYLIN_PROJECT}")
    private String kylinProject;

    @Value("${KYLIN_URL}")
    private String KylinUrl;

    @Value("${KYLIN_JDBC_URL}")
    private String kylinJdbc;

    public String getKylinJdbc() {
        return kylinJdbc;
    }

    public void setKylinJdbc(String kylinJdbc) {
        this.kylinJdbc = kylinJdbc;
    }

    public String getKylinUrl() {
        return KylinUrl;
    }

    public void setKylinUrl(String kylinUrl) {
        KylinUrl = kylinUrl;
    }

    public String getKylinProject() {
        return kylinProject;
    }

    public void setKylinProject(String kylinProject) {
        this.kylinProject = kylinProject;
    }

    public String getKylinOwner() {
        return kylinOwner;
    }

    public void setKylinOwner(String kylinOwner) {
        this.kylinOwner = kylinOwner;
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

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getDataxFloder() {
        return dataxFloder;
    }

    public void setDataxFloder(String dataxFloder) {
        this.dataxFloder = dataxFloder;
    }
}

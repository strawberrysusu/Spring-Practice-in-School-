package config;

import chapter08.ChangePasswordService;
import chapter08.MemberDao;
import chapter08.MemberInfoPrinter;
import chapter08.MemberListPrinter;
import chapter08.MemberPrinter;
import chapter08.MemberRegisterService;
import chapter08.MemberSummaryPrinter;
import chapter08.VersionPrinter;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"db", "chapter08"})
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public MemberDao memberDao(DataSource dataSource) {
        return new MemberDao(dataSource);
    }

    @Bean
    public MemberRegisterService memberRegisterService() {
        return new MemberRegisterService();
    }

    @Bean
    public ChangePasswordService changePasswordService() {
        return new ChangePasswordService();
    }

    @Bean
    public MemberPrinter memberPrinter() {
        return new MemberPrinter();
    }

    @Bean
    public MemberSummaryPrinter summaryPrinter() {
        return new MemberSummaryPrinter();
    }

    @Bean
    public MemberListPrinter memberListPrinter() {
        return new MemberListPrinter();
    }

    @Bean
    public MemberInfoPrinter memberInfoPrinter() {
        return new MemberInfoPrinter();
    }

    @Bean
    public VersionPrinter versionPrinter() {
        VersionPrinter versionPrinter = new VersionPrinter();
        versionPrinter.setMajorVersion(2);
        versionPrinter.setMinorVersion(1);
        return versionPrinter;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        DataSource ds = new DataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3307/daelim?characterEncoding=utf8&serverTimezone=Asia/Seoul");
        ds.setUsername("spring");
        ds.setPassword("daelimspring");
        ds.setInitialSize(2);
        ds.setMaxActive(10);
        ds.setMaxIdle(10);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(1000 * 10);
        ds.setMinEvictableIdleTimeMillis(1000 * 60 * 3);
        ds.setValidationQuery("select 1");
        ds.setValidationQueryTimeout(3);
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

package org.example.examples.chapter05.config;

import org.example.chapter05.AppContext;
import org.example.chapter05.MemberDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "org.example.chapter05",
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AppContext.class),
            @ComponentScan.Filter(
                    type = FilterType.REGEX,
                    pattern = "org\\.example\\.chapter05\\..*Dao")
        })
public class RegexExcludeAppContext extends Chapter05CommonConfig {

    @Bean
    public MemberDao memberDao() {
        return new MemberDao();
    }
}

package org.example.examples.chapter05.config;

import org.example.chapter05.AppContext;
import org.example.chapter05.MemberDao;
import org.example.examples.chapter05.manual.ManualMemberDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "org.example.chapter05",
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = AppContext.class))
public class ManualBeanConflictAppContext extends Chapter05CommonConfig {

    @Bean
    public MemberDao memberDao() {
        return new ManualMemberDao();
    }
}

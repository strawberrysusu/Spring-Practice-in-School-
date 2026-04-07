package org.example.examples.chapter05.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {
            "org.example.examples.chapter05.conflict.chapter03",
            "org.example.examples.chapter05.conflict.chapter05"
        })
public class BeanNameConflictAppContext {
}

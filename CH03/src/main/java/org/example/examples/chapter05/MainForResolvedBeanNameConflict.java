package org.example.examples.chapter05;

import org.example.examples.chapter05.config.ResolvedBeanNameConflictAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForResolvedBeanNameConflict {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(ResolvedBeanNameConflictAppContext.class)) {
            System.out.println(
                    "chapter03MemberRegisterService = "
                            + ctx.containsBean("chapter03MemberRegisterService"));
            System.out.println(
                    "chapter05MemberRegisterService = "
                            + ctx.containsBean("chapter05MemberRegisterService"));
        }
    }
}

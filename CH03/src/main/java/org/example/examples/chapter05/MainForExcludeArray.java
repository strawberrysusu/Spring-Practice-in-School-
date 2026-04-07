package org.example.examples.chapter05;

import java.util.Arrays;
import org.example.chapter05.MemberDao;
import org.example.examples.chapter05.config.ExcludeArrayAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForExcludeArray {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(ExcludeArrayAppContext.class)) {
            String[] beanNames = ctx.getBeanNamesForType(MemberDao.class);
            System.out.println("excludeFilters array example");
            System.out.println("MemberDao bean names: " + Arrays.toString(beanNames));
        }
    }
}

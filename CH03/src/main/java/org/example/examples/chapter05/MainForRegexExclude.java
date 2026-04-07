package org.example.examples.chapter05;

import java.util.Arrays;
import org.example.chapter05.MemberDao;
import org.example.examples.chapter05.config.RegexExcludeAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForRegexExclude {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(RegexExcludeAppContext.class)) {
            String[] beanNames = ctx.getBeanNamesForType(MemberDao.class);
            System.out.println("REGEX exclude example");
            System.out.println("MemberDao bean names: " + Arrays.toString(beanNames));
        }
    }
}

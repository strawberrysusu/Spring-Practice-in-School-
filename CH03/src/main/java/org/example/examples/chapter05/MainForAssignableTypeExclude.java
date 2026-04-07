package org.example.examples.chapter05;

import java.util.Arrays;
import org.example.chapter05.MemberDao;
import org.example.examples.chapter05.config.AssignableTypeExcludeAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForAssignableTypeExclude {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(AssignableTypeExcludeAppContext.class)) {
            String[] beanNames = ctx.getBeanNamesForType(MemberDao.class);
            System.out.println("ASSIGNABLE_TYPE exclude example");
            System.out.println("MemberDao bean names: " + Arrays.toString(beanNames));
        }
    }
}

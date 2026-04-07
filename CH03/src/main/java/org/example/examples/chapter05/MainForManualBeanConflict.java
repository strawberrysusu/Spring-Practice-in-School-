package org.example.examples.chapter05;

import java.util.Arrays;
import org.example.chapter05.MemberDao;
import org.example.examples.chapter05.config.ManualBeanConflictAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForManualBeanConflict {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(ManualBeanConflictAppContext.class)) {
            String[] beanNames = ctx.getBeanNamesForType(MemberDao.class);
            System.out.println("MemberDao bean count: " + beanNames.length);
            System.out.println("MemberDao bean names: " + Arrays.toString(beanNames));
            System.out.println("Actual bean type: " + ctx.getBean(MemberDao.class).getClass().getName());
        }
    }
}

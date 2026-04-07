package main;

import config.AppConfig;
import db.DbQuery;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForTest {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                 new AnnotationConfigApplicationContext(AppConfig.class)) {
            DbQuery dbQuery = ctx.getBean("dbQuery", DbQuery.class);
            System.out.println(dbQuery.count());
        }
    }
}

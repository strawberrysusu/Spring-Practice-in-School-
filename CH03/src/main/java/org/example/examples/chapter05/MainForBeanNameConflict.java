package org.example.examples.chapter05;

import org.example.examples.chapter05.config.BeanNameConflictAppContext;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForBeanNameConflict {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(BeanNameConflictAppContext.class)) {
            System.out.println("Bean count: " + ctx.getBeanDefinitionCount());
        } catch (BeanDefinitionStoreException e) {
            Throwable cause = e.getCause();
            System.out.println("Bean name conflict occurred.");
            System.out.println(cause != null ? cause.getMessage() : e.getMessage());
        }
    }
}

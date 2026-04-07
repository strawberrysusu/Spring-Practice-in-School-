package main;

import chapter07.Calculator;
import config.AppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainAspect {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(AppContext.class);

        Calculator calculator = ctx.getBean("calculator", Calculator.class);
        long result = calculator.factorial(5);
        System.out.println("cal.factorial(5) = " + result);
        System.out.println(calculator.getClass().getName());

        ctx.close();
    }
}

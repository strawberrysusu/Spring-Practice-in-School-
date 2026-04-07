package org.example.chapter02;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

//        Greeter greeter = new Greeter();
//        greeter.setFormat("%s, 안녕하세요!");
//        String msg = greeter.greet("스프링");
//        System.out.println(msg);


        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(AppContext.class);
        Greeter greeter = ctx.getBean("greeter", Greeter.class);
        String msg = greeter.greet("스프링");
        System.out.println(msg);

        Greeter greeter2 = ctx.getBean("greeter", Greeter.class);
        System.out.println(greeter == greeter2);
    }
}

package main;

import chapter08.ChangePasswordService;
import chapter08.MemberNotFoundException;
import chapter08.WrongPasswordException;
import config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForChangePassword {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                 new AnnotationConfigApplicationContext(AppConfig.class)) {
            ChangePasswordService changePasswordService =
                ctx.getBean("changePasswordService", ChangePasswordService.class);

            try {
                changePasswordService.changePassword("a@a.com", "0000", "1234");
                System.out.println("Password changed.");
            } catch (MemberNotFoundException e) {
                System.out.println("Member not found.");
            } catch (WrongPasswordException e) {
                System.out.println("Wrong password.");
            }
        }
    }
}

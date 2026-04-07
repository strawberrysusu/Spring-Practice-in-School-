package org.example.chapter05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainForSpring {

    private static final ApplicationContext ctx =
            new AnnotationConfigApplicationContext(AppContext.class);

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter command:");
            String command = reader.readLine();
            if (command == null || command.equalsIgnoreCase("exit")) {
                System.out.println("Exit.");
                break;
            }
            if (command.startsWith("new ")) {
                processNewCommand(command.split(" "));
                continue;
            }
            if (command.startsWith("change ")) {
                processChangeCommand(command.split(" "));
                continue;
            }
            if (command.equals("list")) {
                processListCommand();
                continue;
            }
            if (command.startsWith("info ")) {
                processInfoCommand(command.split(" "));
                continue;
            }
            if (command.equals("version")) {
                processVersionCommand();
                continue;
            }
            printHelp();
        }
    }

    private static void processNewCommand(String[] args) {
        if (args.length != 5) {
            printHelp();
            return;
        }

        MemberRegisterService memberRegisterService =
                ctx.getBean("memberRegisterService", MemberRegisterService.class);
        RegisterRequest req = new RegisterRequest();
        req.setEmail(args[1]);
        req.setName(args[2]);
        req.setPassword(args[3]);
        req.setConfirmPassword(args[4]);

        if (!req.isPasswordEqualToConfirmPassword()) {
            System.out.println("Password and confirmation do not match.");
            return;
        }

        try {
            memberRegisterService.regist(req);
            System.out.println("Registration complete.");
        } catch (DuplicationMemberException e) {
            System.out.println("Email already exists.");
        }
    }

    private static void processChangeCommand(String[] args) {
        if (args.length != 4) {
            printHelp();
            return;
        }

        ChangePasswordService changePasswordService =
                ctx.getBean("changePasswordService", ChangePasswordService.class);
        try {
            changePasswordService.changePassword(args[1], args[2], args[3]);
            System.out.println("Password changed.");
        } catch (MemberNotFoundException e) {
            System.out.println("Member not found.");
        } catch (WrongPasswordException e) {
            System.out.println("Current password is incorrect.");
        }
    }

    private static void processListCommand() {
        MemberListPrinter memberListPrinter =
                ctx.getBean("memberListPrinter", MemberListPrinter.class);
        memberListPrinter.printAll();
    }

    private static void processInfoCommand(String[] args) {
        if (args.length != 2) {
            printHelp();
            return;
        }

        MemberInfoPrinter memberInfoPrinter =
                ctx.getBean("memberInfoPrinter", MemberInfoPrinter.class);
        memberInfoPrinter.printMemberInfo(args[1]);
    }

    private static void processVersionCommand() {
        VersionPrinter versionPrinter = ctx.getBean("versionPrinter", VersionPrinter.class);
        versionPrinter.print();
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Invalid command. Use one of the commands below.");
        System.out.println("new email name password confirmPassword");
        System.out.println("change email currentPassword newPassword");
        System.out.println("list");
        System.out.println("info email");
        System.out.println("version");
        System.out.println();
    }
}

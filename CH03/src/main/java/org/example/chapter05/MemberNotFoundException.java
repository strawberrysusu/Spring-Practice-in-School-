package org.example.chapter05;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("Member does not exist.");
    }
}

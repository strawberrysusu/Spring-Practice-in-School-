package org.example.chapter05;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("Current password does not match.");
    }
}

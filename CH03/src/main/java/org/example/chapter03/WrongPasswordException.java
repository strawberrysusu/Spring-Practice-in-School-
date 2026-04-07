package org.example.chapter03;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}

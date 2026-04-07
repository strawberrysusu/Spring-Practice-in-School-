package org.example.chapter03;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("해당 이메일의 회원이 없습니다.");
    }
}

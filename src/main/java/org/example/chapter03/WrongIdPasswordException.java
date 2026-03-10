package org.example.chapter03;

// 현재 비밀번호가 일치하지 않을 때 사용하는 예외다.
// IllegalArgumentException도 쓸 수는 있지만,
// 이 예외는 "비밀번호 확인 실패"라는 도메인 의미를 이름으로 바로 드러낸다.
public class WrongIdPasswordException extends RuntimeException {

    public WrongIdPasswordException() {
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}

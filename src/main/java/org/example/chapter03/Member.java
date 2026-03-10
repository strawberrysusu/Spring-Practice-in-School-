package org.example.chapter03;

import java.time.LocalDateTime;

public class Member {

    private Long id;
    private String name;
    private String password;
    private String email;
    private LocalDateTime registerDateTime;


    public Member(String name, String password, String email, LocalDateTime registerDateTime) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.registerDateTime = registerDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    // 이 setter는 전달받은 값을 그대로 넣는다.
    // 즉, "기존 비밀번호가 맞는지" 같은 검증 없이 바로 바꾼다.
    // 단순 저장/세팅 용도라면 가능하지만, 비밀번호 변경 규칙을 표현하기에는 약하다.
    public void setPassword(String password) {
        this.password = password;
    }

    // 비밀번호 변경은 단순 대입과 성격이 다르다.
    // 먼저 사용자가 입력한 기존 비밀번호(oldPassword)가 현재 비밀번호와 같은지 검사한다.
    // 틀리면 예외를 던져서 메서드를 즉시 끝내고, 맞을 때만 새 비밀번호로 변경한다.
    public void changePassword(String oldPassword, String newPassword) {
        if (!password.equals(oldPassword)) {
            throw new WrongIdPasswordException();
        }

        this.password = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegisterDateTime() {
        return registerDateTime;
    }

    public void setRegisterDateTime(LocalDateTime registerDateTime) {
        this.registerDateTime = registerDateTime;
    }
}

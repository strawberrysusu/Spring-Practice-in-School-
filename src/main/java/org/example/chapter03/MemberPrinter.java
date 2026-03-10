package org.example.chapter03;

public class MemberPrinter {

    public void print(Member member){
        System.out.printf("회원정보 : 아이디 = %d, 이메일 = %s, 이름 = %s",
                member.getId(),member.getEmail(),member.getName());
    }
}

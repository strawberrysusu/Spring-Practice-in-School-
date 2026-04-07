package org.example.chapter05;

public class MemberSummaryPrinter extends MemberPrinter {

    @Override
    public void print(Member member) {
        System.out.printf(
                "Member info: email=%s, name=%s%n",
                member.getEmail(),
                member.getName());
    }
}

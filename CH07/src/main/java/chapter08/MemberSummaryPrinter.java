package chapter08;

public class MemberSummaryPrinter extends MemberPrinter {

    @Override
    public void print(Member member) {
        System.out.printf("Member summary: email=%s, name=%s%n",
            member.getEmail(), member.getName());
    }
}

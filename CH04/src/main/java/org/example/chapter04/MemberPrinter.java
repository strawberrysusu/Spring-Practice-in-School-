package org.example.chapter04;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberPrinter {

    private DateTimeFormatter dateTimeFormatter;

    public void print(Member member) {
        if (dateTimeFormatter == null) {
            System.out.printf(
                    "회원 정보 : 아이디 = %d, 이메일 = %s, 이름 = %s, 등록일 = %tF%n",
                    member.getId(),
                    member.getEmail(),
                    member.getName(),
                    member.getRegisterDateTime());
            return;
        }

        System.out.printf(
                "회원 정보 : 아이디 = %d, 이메일 = %s, 이름 = %s, 등록일 = %s%n",
                member.getId(),
                member.getEmail(),
                member.getName(),
                dateTimeFormatter.format(member.getRegisterDateTime()));
    }

    @Autowired(required = false)
    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }
}

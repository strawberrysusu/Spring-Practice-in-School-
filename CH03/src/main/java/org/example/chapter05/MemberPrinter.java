package org.example.chapter05;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberPrinter {

    private DateTimeFormatter dateTimeFormatter;

    public void print(Member member) {
        if (dateTimeFormatter == null) {
            System.out.printf(
                    "Member info: id=%d, email=%s, name=%s, registered=%tF%n",
                    member.getId(),
                    member.getEmail(),
                    member.getName(),
                    member.getRegisterDateTime());
            return;
        }

        System.out.printf(
                "Member info: id=%d, email=%s, name=%s, registered=%s%n",
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

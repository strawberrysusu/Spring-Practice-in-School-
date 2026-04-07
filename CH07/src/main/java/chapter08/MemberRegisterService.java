package chapter08;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberRegisterService {

    @Autowired
    private MemberDao memberDao;

    public void regist(RegisterRequest req) {
        Member member = memberDao.selectByEmail(req.getEmail());
        if (member != null) {
            throw new DuplicationMemberException("Duplication Email : " + req.getEmail());
        }

        Member newMem = new Member(
            req.getEmail(),
            req.getPassword(),
            req.getName(),
            LocalDateTime.now()
        );
        memberDao.insert(newMem);
    }
}

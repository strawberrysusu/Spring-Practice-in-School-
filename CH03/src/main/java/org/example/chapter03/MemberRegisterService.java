package org.example.chapter03;

import java.time.LocalDateTime;

public class MemberRegisterService {

    private final MemberDao memberDao;

    public MemberRegisterService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void regist(RegisterRequest req) {
        Member member = memberDao.selectByEmail(req.getEmail());
        if (member != null) {
            throw new DuplicationMemberException("Duplication Email : " + req.getEmail());
        }

        Member newMember =
                new Member(req.getEmail(), req.getPassword(), req.getName(), LocalDateTime.now());
        memberDao.insert(newMember);
    }
}

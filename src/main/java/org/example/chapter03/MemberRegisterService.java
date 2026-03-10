package org.example.chapter03;

import java.time.LocalDateTime;

public class MemberRegisterService {

  // 의존
    private final MemberDao memberDao;

  // DI 의존하는 객체 직접 생성대신 의존객체를 전달받는 방식
    public MemberRegisterService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void regist(RegisterRequest req){
        //이메일로 회원 데이터 조회
        Member member = memberDao.selectByEmail(req.getEmail());
        // 이미 같은 이메일 가진 회원이 존재하면 예외 발생
        if(member != null){
            throw new DuplicateMemberException("Duplicate Email : "+req.getEmail());
        }
        // 회원이 존재하지 않으면 등록 처리
        // Member 생성자 순서는 name, password, email 이다.
        Member newMember = new Member(req.getName(), req.getPassword(), req.getEmail(), LocalDateTime.now());
        memberDao.insert(newMember);
    }
}

package org.example.chapter03;

public class ChangePasswordService {
    private MemberDao memberDao;
    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

  public void changePassword(String email, String oldPassword, String newPassword) {
        // email 로 회원 조회
      Member member = memberDao.selectByEmail(email);
      // 회원이 없으면 예외 발생
      if (member == null) {
          throw new MemberNotFoundException();
      }
      // 회원이 있으면 비밀번호 변경 처리
      member.changePassword(oldPassword, newPassword);
  }

}

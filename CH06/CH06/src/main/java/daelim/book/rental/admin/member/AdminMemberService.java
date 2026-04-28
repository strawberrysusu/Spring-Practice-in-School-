package daelim.book.rental.admin.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminMemberService {

    public static final int ADMIN_ACCOUNT_ALREADY_EXISTS = 0;
    public static final int ADMIN_ACCOUNT_CREATE_SUCCESS = 1;
    public static final int ADMIN_ACCOUNT_CREATE_FAIL = -1;

    @Autowired
    private AdminMemberDao adminMemberDao;

    public int createAccount(AdminMemberVo adminMemberVo) {
        System.out.println("[AdminMemberService] createAccount");

        boolean isMember = adminMemberDao.exsitAdminAccount(adminMemberVo.getId());
        if (!isMember) {
            int result = adminMemberDao.insertAdminAccount(adminMemberVo);
            if (result > 0) {
                System.out.println("[AdminMemberService] createAccount success");
                return ADMIN_ACCOUNT_CREATE_SUCCESS;
            }

            System.out.println("[AdminMemberService] createAccount fail");
            return ADMIN_ACCOUNT_CREATE_FAIL;
        }

        System.out.println("[AdminMemberService] createAccount already exists");
        return ADMIN_ACCOUNT_ALREADY_EXISTS;
    }
}

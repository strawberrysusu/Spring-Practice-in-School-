package daelim.book.rental.admin.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Autowired
    private AdminMemberService adminMemberService;

    @RequestMapping("/createAccountForm")
    public String createAccountForm() {
        System.out.println("[AdminMemberController.createAccountForm]");
        return "admin/member/create_account_form";
    }

    @RequestMapping("/loginForm")
    public String loginForm() {
        System.out.println("[AdminMemberController.loginForm]");
        return "admin/member/login_form";
    }

    @RequestMapping(value = "/createAccountConfirm", method = RequestMethod.POST)
    public String createAccountConfirm(AdminMemberVo adminMemberVo) {
        System.out.println("[AdminMemberController.createAccountConfirm]");

        String nextPage = "admin/member/create_account_ok";
        int result = adminMemberService.createAccount(adminMemberVo);
        if (result <= 0) {
            nextPage = "admin/member/create_account_ng";
        }

        return nextPage;
    }
}

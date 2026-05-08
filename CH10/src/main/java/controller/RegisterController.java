package controller;

import chapter10.DuplicationMemberException;
import chapter10.MemberRegisterService;
import chapter10.RegisterRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private MemberRegisterService memberRegisterService;

    @RequestMapping("/step1")
    public String handleStep1() {
        System.out.println("step1 >>>");
        return "register/step1";
    }

    @GetMapping("/step2")
    public String handleStep2Get() {
        System.out.println("[GET] step2 >>>");
        return "redirect:/register/step1";
    }

    @PostMapping("/step2")
    public String handleStep2(@RequestParam(value = "agree", defaultValue = "false") Boolean agree,
        Model model) {
        System.out.println("[POST] step2 >>>");
        System.out.println("step2::agree = " + agree);
        if (!agree) {
            return "register/step1";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register/step2";
    }

    @PostMapping("/step3")
    public String handleStep3(@Valid RegisterRequest registerRequest, Errors errors) {
        System.out.println("[POST] step3 >>>");
        if (errors.hasErrors()) {
            return "register/step2";
        }
        try {
            memberRegisterService.regist(registerRequest);
            return "register/step3";
        } catch (DuplicationMemberException ex) {
            errors.rejectValue("email", "duplicate");
            return "register/step2";
        }
    }
}

package daelim.book.rental.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @RequestMapping(value = {"", "/"})
    public String home() {
        System.out.println("[AdminHomeController] home()");
        return "admin/home";
    }
}

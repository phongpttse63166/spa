package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp490.spa.entities.User;
import swp490.spa.services.UserService;
import swp490.spa.utils.helper.ResponseHelper;
import swp490.spa.utils.support.Response;

@RestController
@RequestMapping("/user")
public class UserController {
    private Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public Response login(@RequestParam String phone, @RequestParam String password, @RequestParam String role){
        User userLogin = userService.findByPhoneAndPassword(phone, password, role);
        return ResponseHelper.ok(userLogin);
    }
}

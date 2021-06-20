package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Consultant;
import swp490.spa.entities.Customer;
import swp490.spa.entities.User;
import swp490.spa.services.ConsultantService;
import swp490.spa.services.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin
public class ConsultantController {
    @Autowired
    ConsultantService consultantService;
    @Autowired
    UserService userService;

    public ConsultantController(ConsultantService consultantService, UserService userService) {
        this.consultantService = consultantService;
        this.userService = userService;
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account){
        Consultant consultant = consultantService.findByConsultantId(account.getId());
        User oldUser = consultant.getUser();
        User updateUser = consultant.getUser();
        updateUser.setPassword(account.getPassword());
        if(Objects.nonNull(userService.editUser(updateUser))){
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }
}

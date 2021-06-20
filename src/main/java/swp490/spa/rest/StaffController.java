package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Staff;
import swp490.spa.entities.User;
import swp490.spa.services.StaffService;
import swp490.spa.services.UserService;
import swp490.spa.utils.support.Notification;

import java.util.Objects;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserService userService;
    private Conversion conversion;

    public StaffController(StaffService staffService, UserService userService) {
        this.staffService = staffService;
        this.userService = userService;
        this.conversion = new Conversion();
    }

    @GetMapping("/staff/findbyId")
    public Response findStaffById(@RequestParam Integer userId){
        Staff staff = staffService.findByStaffId(userId);
        return ResponseHelper.ok(staff);
    }

    @PutMapping("/staff/editprofile")
    public Response editProfileStaff(@RequestBody User user){
        Staff staffResult = staffService.findByStaffId(user.getId());
        if(Objects.nonNull(staffResult)){
            User userResult = staffResult.getUser();
            userResult.setFullname(user.getFullname());
            userResult.setEmail(user.getEmail());
            userResult.setAddress(user.getAddress());
            userResult.setBirthdate(user.getBirthdate());
            userResult.setGender(user.getGender());
            if(Objects.nonNull(userService.editUser(user))){
                return ResponseHelper.ok(userResult);
            }
            return ResponseHelper.error(Notification.EDIT_PROFILE_FAIL);
        }
        return ResponseHelper.error(Notification.STAFF_NOT_EXISTED);
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account){
        Staff staff = staffService.findByStaffId(account.getId());
        User oldUser = staff.getUser();
        User updateUser = staff.getUser();
        updateUser.setPassword(account.getPassword());
        if(Objects.nonNull(userService.editUser(updateUser))){
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }
}

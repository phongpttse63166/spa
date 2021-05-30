package swp490.spa.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/manager")
@RestController
@CrossOrigin
public class ManagerController {
    @GetMapping(value = "test")
    public String testJWT(){
        return "Thành công";
    }
}

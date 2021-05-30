package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.responses.LoginResponse;
import swp490.spa.entities.Category;
import swp490.spa.entities.User;
import swp490.spa.jwt.JWTUtils;
import swp490.spa.repositories.UserRepository;
import swp490.spa.services.CategoryService;
import swp490.spa.services.UserService;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;

@RestController
@RequestMapping("/public")
@CrossOrigin
public class PublicController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    private Conversion conversion;

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    public PublicController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.conversion = new Conversion();
    }

    @GetMapping("/category")
    public Response findCategoryByStatus(@RequestParam String status, Pageable pageable){
        Page<Category> categories = categoryService.findAllByStatus(status, pageable);
        if (!categories.hasContent() && !categories.isFirst()) {
            categories = categoryService.findAllByStatus(status,
                    PageRequest.of(categories.getTotalPages()-1, categories.getSize(), categories.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToCategoryResponse(categories));
    }

    @GetMapping("/user")
    public Response findUserByPhone(@RequestParam String phone){
        User user = userService.findByPhone(phone);
        return ResponseHelper.ok(user);
    }

    @GetMapping("/login")
    public LoginResponse login (@RequestParam("phone") String phone, @RequestParam("password") String password){
        User newAccount = userRepository.findByPhone(phone);

        if(newAccount == null){
            return LoginResponse.createErrorResponse(LoginResponse.Error.USERNAME_NOT_FOUND);
        }

        if(!newAccount.getPassword().equals(password)){
            return LoginResponse.createErrorResponse(LoginResponse.Error.WRONG_PASSWORD);
        }

        String role = newAccount.getRole().name();
        String token = jwtUtils.generateToken(newAccount.getPhone());
        int idAccount = newAccount.getId();

        return LoginResponse.createSuccessResponse(token,role,idAccount);
    }
}

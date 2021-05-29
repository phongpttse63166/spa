package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.entities.Category;
import swp490.spa.services.CategoryService;
import swp490.spa.services.UserService;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    private Conversion conversion;


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


}

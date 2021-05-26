package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp490.spa.entities.Category;
import swp490.spa.responses.CategoryResponse;
import swp490.spa.services.CategoryService;
import swp490.spa.services.UserService;
import swp490.spa.utils.helper.ResponseHelper;
import swp490.spa.utils.support.Response;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PublicController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    public PublicController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/findCategoryByStatus/{status}")
    public Response findCategoryByStatus(@RequestParam String status, Pageable pageable){
        Page<Category> categories = categoryService.findAllByStatus(status, pageable);
        if (!categories.hasContent() && !categories.isFirst()) {
            categories = categoryService.findAllByStatus(status,
                    PageRequest.of(categories.getTotalPages()-1, categories.getSize(), categories.getSort()));
        }

        return ResponseHelper.ok(convertToCategoryResponse(categories));
    }

    private Page<CategoryResponse> convertToCategoryResponse(Page<Category> categories) {
        List<CategoryResponse> categoryData = categories.getContent().stream()
                .map(category -> new CategoryResponse(  category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getStatus()))
                .collect(Collectors.toList());

        long totalElements = categories.getTotalElements();
        return new PageImpl<>(categoryData, totalElements == 0 ? Pageable.unpaged() : categories.getPageable(), totalElements);
    }
}

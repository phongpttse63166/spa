package swp490.spa.dto.support;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import swp490.spa.dto.responses.CategoryResponse;
import swp490.spa.dto.responses.UserResponse;
import swp490.spa.entities.Category;
import swp490.spa.entities.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Conversion {

    public Page<CategoryResponse> convertToCategoryResponse(Page<Category> categories) {
        List<CategoryResponse> categoryData = categories.getContent().stream()
                .map(category -> new CategoryResponse(  category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getStatus()))
                .collect(Collectors.toList());

        long totalElements = categories.getTotalElements();
        return new PageImpl<>(categoryData,
                totalElements == 0 ? Pageable.unpaged() : categories.getPageable(),
                totalElements);
    }

    public Page<UserResponse> convertToUserResponse(Page<User> users) {
        List<UserResponse> userData = users.getContent().stream()
                .map(user -> new UserResponse(user.getId(),
                        user.getFullname(),
                        user.getPhone(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getAddress(),
                        user.getRole()))
                .collect(Collectors.toList());

        long totalElements = users.getTotalElements();
        return new PageImpl<>(userData,
                totalElements == 0 ? Pageable.unpaged() : users.getPageable(),
                totalElements);
    }
}

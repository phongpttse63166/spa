package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.CategoryResponse;
import swp490.spa.entities.Category;

@Mapper
public interface CategoryMapper {
    CategoryResponse changeToCategoryResponse(Category category);
}

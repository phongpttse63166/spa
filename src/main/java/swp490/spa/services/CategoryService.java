package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Category;
import swp490.spa.entities.Status;
import swp490.spa.repositories.CategoryRepository;

import java.net.ContentHandler;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> findAllByStatus(Status status, Pageable pageable){
        return this.categoryRepository.findByStatusOrderById(status, pageable);
    }

    public Category findById(Integer categoryId) {
        return this.categoryRepository.findByCategoryId(categoryId);
    }

    public Page<Category> findCategoryBySpaId(Integer spaId, Status status,
                                              String search, Pageable pageable) {
        return this.categoryRepository
                .findBySpa_IdAndStatusAndNameContainingOrderById(spaId, status, search, pageable);
    }

    public Category editByCategoryId(Category category) {
        return this.categoryRepository.save(category);
    }

    public boolean removeCategory(Category categoryResult) {
        if(Objects.nonNull(this.categoryRepository.save(categoryResult))){
            return true;
        }
        return false;
    }

    public Category insertNewCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public Page<Category> findBySpaIdAndStatusNoSearch(Integer spaId, Status status, Pageable pageable) {
        return this.categoryRepository.findBySpa_IdAndStatusOrderById(spaId, status, pageable);
    }
}

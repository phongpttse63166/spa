package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Category;
import swp490.spa.entities.Status;
import swp490.spa.repositories.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> findAllByStatus(Status status, Pageable pageable){
        return this.categoryRepository.findByStatus(status, pageable);
    }

    public Category findById(Integer categoryId) {
        return this.categoryRepository.findByCategoryId(categoryId);
    }

    public Page<Category> findCategoryBySpaId(Integer spaId, Status status, Pageable pageable) {
        return this.categoryRepository.findBySpa_IdAndStatusOrderById(spaId, status, pageable);
    }
}

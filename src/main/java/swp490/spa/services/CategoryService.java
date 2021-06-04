package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Category;
import swp490.spa.repositories.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> findAllByStatus(String status, Pageable pageable){
        return this.categoryRepository.findByStatus(status, pageable);
    }

//    public List<Category> findAllByStatus(String status){
//        return this.categoryRepository.findByStatus(status);
//    }
}

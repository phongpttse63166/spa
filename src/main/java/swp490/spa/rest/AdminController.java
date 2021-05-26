package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class AdminController {
    Logger logger = LogManager.getLogger(AdminController.class);

}

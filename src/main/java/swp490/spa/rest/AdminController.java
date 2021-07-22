package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.CategoryRequest;
import swp490.spa.dto.requests.SpaPackageRequest;
import swp490.spa.dto.requests.SpaServiceRequest;
import swp490.spa.dto.requests.SpaTreatmentRequest;
import swp490.spa.dto.responses.CategorySpaPackageResponse;
import swp490.spa.dto.responses.SpaPackageTreatmentResponse;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    Logger LOGGER = LogManager.getLogger(AdminController.class);
    @Autowired
    private ManagerService managerService;
    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private SpaPackageService spaPackageService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TreatmentServiceService treatmentServiceService;
    private Conversion conversion;

    public AdminController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService,
                             TreatmentServiceService treatmentServiceService){
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaPackageService = spaPackageService;
        this.spaTreatmentService = spaTreatmentService;
        this.treatmentServiceService = treatmentServiceService;
        this.conversion = new Conversion();
    }

    @GetMapping("/spaService/findByStatus")
    public Response findSpaServiceByStatus(@RequestParam Status status,
                                           @RequestParam String search,
                                           Pageable pageable) {
        Page<swp490.spa.entities.SpaService> spaServices =
                spaServiceService.findByStatus(status, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices =
                    spaServiceService.findByStatus(status, search,
                            PageRequest.of(spaServices.getTotalPages() - 1,
                                    spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @PostMapping(value = "/spaservice/insert", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response createNewSpaService(SpaServiceRequest spaServiceRequest) {
        if (Objects.nonNull(spaServiceRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(spaServiceRequest.getFile());
            if (imageLink != "") {
                Manager manager = managerService.findManagerById(spaServiceRequest.getCreateBy());
                if (Objects.isNull(manager)) {
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.MANAGER));
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.MANAGER));
                }
                swp490.spa.entities.SpaService spaService = new swp490.spa.entities.SpaService();
                spaService.setName(spaServiceRequest.getName());
                spaService.setDescription(spaServiceRequest.getDescription());
                spaService.setPrice(spaServiceRequest.getPrice());
                spaService.setStatus(spaServiceRequest.getStatus());
                spaService.setType(spaServiceRequest.getType());
                spaService.setDurationMin(spaServiceRequest.getDurationMin());
                spaService.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                spaService.setCreateBy(spaServiceRequest.getCreateBy().toString());
                spaService.setImage(imageLink);
                swp490.spa.entities.SpaService serviceResult = spaServiceService.insertNewSpaService(spaService);
                if (Objects.nonNull(serviceResult)) {
                    return ResponseHelper.ok(serviceResult);
                }
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SERVICE));
    }

    @GetMapping("/spaPackage/findByServiceId")
    public Response findSpaPackageBySpaServiceId(@RequestParam Integer spaServiceId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String search) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllBySpaServiceId(spaServiceId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @PutMapping("/spaservice/delete/{spaServiceId}")
    public Response removeSpaService(@PathVariable Integer spaServiceId) {
        swp490.spa.entities.SpaService spaServiceResult = spaServiceService.findBySpaServiceId(spaServiceId);
        if (Objects.nonNull(spaServiceResult)) {
            spaServiceResult.setStatus(Status.DISABLE);
            if (spaServiceService.removeService(spaServiceResult)) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.SERVICE));
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_FAILED, Constant.SERVICE));
    }

    @PutMapping(value = "/spaservice/edit/{spaServiceId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editSpaService(@PathVariable Integer spaServiceId, SpaServiceRequest spaService) {
        swp490.spa.entities.SpaService spaServiceEdit = spaServiceService.findBySpaServiceId(spaServiceId);
        if (Objects.nonNull(spaServiceEdit)) {
            if (Objects.nonNull(spaService.getFile())) {
                String imageLink = UploadImage.uploadImage(spaService.getFile());
                if (imageLink != "") {
                    spaServiceEdit.setImage(imageLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(spaService.getName())) {
                spaServiceEdit.setName(spaService.getName());
            }
            if (Objects.nonNull(spaService.getDescription())) {
                spaServiceEdit.setDescription(spaService.getDescription());
            }
            if (Objects.nonNull(spaService.getDurationMin())) {
                spaServiceEdit.setDurationMin(spaService.getDurationMin());
            }
            if (Objects.nonNull(spaService.getPrice())) {
                spaServiceEdit.setPrice(spaService.getPrice());
            }
            swp490.spa.entities.SpaService spaServiceResult = spaServiceService.editBySpaService(spaServiceEdit);
            if (Objects.nonNull(spaServiceResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SERVICE));
                return ResponseHelper.ok(spaServiceResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SERVICE));
    }

    @GetMapping("/categorySpaPackages/findByStatus")
    public Response findCategorySpaPackagesByStatus(@RequestParam Status status,
                                                    @RequestParam String search,
                                                    Pageable pageable) {
        List<Category> categories;
        long totalItem = 0;
        if (search == "") {
            totalItem = categoryService.findAllByStatus(status,
                    PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                    .getContent().size();
            categories = categoryService.findAllByStatus(status, pageable)
                    .getContent();
        } else {
            totalItem =
                    categoryService.findCategoryByStatusAndName(status, search,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                            .getContent().size();
            categories =
                    categoryService.findCategoryByStatusAndName(status, search, pageable).getContent();
        }
        if (Objects.nonNull(categories)) {
            List<CategorySpaPackageResponse> categorySpaPackageResponses = new ArrayList<>();
            for (Category category : categories) {
                List<SpaPackage> spaPackages = spaPackageService.findByCategoryId(category.getId());
                CategorySpaPackageResponse cspr = new CategorySpaPackageResponse(category, spaPackages);
                categorySpaPackageResponses.add(cspr);
            }
            Page<CategorySpaPackageResponse> pageReturn =
                    new PageImpl<>(categorySpaPackageResponses, pageable,
                            totalItem);
            return ResponseHelper.ok(pageReturn);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
    }

    @PostMapping(value = "/category/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewCategory(CategoryRequest categoryRequest) {
        if (Objects.nonNull(categoryRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(categoryRequest.getFile());
            if (imageLink != "") {
                Category categoryNew = new Category();
                categoryNew.setName(categoryRequest.getName());
                categoryNew.setIcon(imageLink);
                categoryNew.setDescription(categoryRequest.getDescription());
                categoryNew.setStatus(Status.AVAILABLE);
                categoryNew.setCreateBy(categoryRequest.getCreateBy());
                categoryNew.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                Category categoryResult = categoryService.insertNewCategory(categoryNew);
                if (Objects.nonNull(categoryResult)) {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.CATEGORY));
                    return ResponseHelper.ok(categoryResult);
                }
                LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
    }

    @PutMapping(value = "/category/edit/{categoryId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editCategory(@PathVariable Integer categoryId,
                                 CategoryRequest category) {
        Category categoryEdit = categoryService.findById(categoryId);
        if (Objects.nonNull(categoryEdit)) {
            if (Objects.nonNull(category.getFile())) {
                String iconLink = UploadImage.uploadImage(category.getFile());
                if (iconLink != "") {
                    categoryEdit.setIcon(iconLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(category.getName())) {
                categoryEdit.setName(category.getName());
            }
            if (Objects.nonNull(category.getDescription())) {
                categoryEdit.setDescription(category.getDescription());
            }
            Category categoryResult = categoryService.editByCategoryId(categoryEdit);
            if (Objects.nonNull(categoryResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CATEGORY));
                return ResponseHelper.ok(categoryResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CATEGORY));
    }

    @GetMapping("/spaPackageTreatment/findByStatus")
    public Response findSpaPackageTreatmentByStatus(@RequestParam String search,
                                                    Pageable pageable) {
        List<SpaPackageTreatmentResponse> result = new ArrayList<>();
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageByStatus(Status.AVAILABLE, search, pageable);
        long totalItem =
                spaPackageService.findSpaPackageByStatus(Status.AVAILABLE,
                        Constant.SEARCH_NO_CONTENT,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                        .getTotalElements();
        if (spaPackages.getContent().size() != 0 && !spaPackages.getContent().isEmpty()) {
            for (SpaPackage spaPackage : spaPackages.getContent()) {
                List<SpaTreatment> spaTreatments = new ArrayList<>();
                SpaPackageTreatmentResponse sptr = new SpaPackageTreatmentResponse();
                sptr.setSpaPackage(spaPackage);
                spaTreatments =
                        spaTreatmentService.findByPackageId(spaPackage.getId(),
                                Constant.SEARCH_NO_CONTENT,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT)).toList();
                sptr.setSpaTreatments(spaTreatments);
                result.add(sptr);
            }
        }
        Page<SpaPackageTreatmentResponse> page = new PageImpl<>(result, pageable, totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageTreatmentResponse(page));
    }

    @GetMapping("/spaService/findByType")
    public Response findSpaServiceByType(@RequestParam Type type,
                                         @RequestParam String search,
                                         Pageable pageable) {
        Page<swp490.spa.entities.SpaService> spaServices = spaServiceService.findByType(type, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices =
                    spaServiceService.findByType(type, search,
                            PageRequest.of(spaServices.getTotalPages() - 1,
                                    spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @GetMapping("/spaservices/findbyid/{packageId}")
    public Response findSpaServicesBySpaPackageId(@PathVariable Integer packageId) {
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackage)) {
            List<swp490.spa.entities.SpaService> spaServices = spaPackage.getSpaServices();
            Page<swp490.spa.entities.SpaService> spaServicePage =
                    new PageImpl<>(spaServices,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                            spaServices.size());
            return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServicePage));
        }
        LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICES));
    }

    @PostMapping(value = "/spapackageservices/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewSpaPackageWithServices(SpaPackageRequest spaPackage) {
        if (Objects.nonNull(spaPackage.getFile())) {
            String imageLink = UploadImage.uploadImage(spaPackage.getFile());
            if (imageLink != "") {
                List<swp490.spa.entities.SpaService> spaServices = new ArrayList<>();
                Category category = categoryService.findById(spaPackage.getCategoryId());
                if (Objects.isNull(category)) {
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
                }
                SpaPackage spaPackageInsert = new SpaPackage();
                spaPackageInsert.setName(spaPackage.getName());
                spaPackageInsert.setCategory(category);
                spaPackageInsert.setCreate_by(spaPackage.getCreateBy());
                spaPackageInsert.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                spaPackageInsert.setDescription(spaPackage.getDescription());
                spaPackageInsert.setImage(imageLink);
                spaPackageInsert.setType(spaPackage.getType());
                spaPackageInsert.setStatus(spaPackage.getStatus());
                SpaPackage spaPackageResult = spaPackageService.insertNewSpaPackage(spaPackageInsert);
                if (Objects.isNull(spaPackageResult)) {
                    return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SPA_PACKAGE_SERVICE));
                }
                for (Integer serviceId : spaPackage.getListSpaServiceId()) {
                    swp490.spa.entities.SpaService spaService = spaServiceService.findById(serviceId);
                    if (Objects.isNull(spaService)) {
                        ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
                    }
                    spaServices.add(spaService);
                }
                spaPackageResult.addListService(spaServices);
                if (Objects.nonNull(spaPackageService.insertNewSpaPackage(spaPackageResult))) {
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.SPA_PACKAGE_SERVICE));
                }
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SPA_PACKAGE_SERVICE));
    }

    @PostMapping("/spatreatmentservices/insert")
    public Response insertNewSpaPackageWithServices(@RequestBody SpaTreatmentRequest spaTreatmentRequest) {
        List<TreatmentService> treatmentServices = new ArrayList<>();
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaTreatmentRequest.getPackageId());
        if (Objects.isNull(spaPackage)) {
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        }
        int ordinal = 1;
        int totalTime = 0;
        double totalPrice = 0.0;
        for (int i = 0; i < spaTreatmentRequest.getListSpaServiceId().size(); i++) {
            swp490.spa.entities.SpaService spaService =
                    spaServiceService.findById(spaTreatmentRequest.getListSpaServiceId().get(i));
            if (Objects.isNull(spaService)) {
                ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
            }
            TreatmentService treatmentService = new TreatmentService(spaService, ordinal);
            ordinal++;
            treatmentServices.add(treatmentService);
            totalTime = totalTime + spaService.getDurationMin();
            totalPrice = totalPrice + spaService.getPrice();
        }
        SpaTreatment spaTreatmentInsert = new SpaTreatment(spaTreatmentRequest.getName(),
                spaTreatmentRequest.getDescription(), totalTime,
                Date.valueOf(LocalDateTime.now().toLocalDate()),
                spaTreatmentRequest.getCreateBy(),
                spaPackage, treatmentServices, totalPrice);
        if (Objects.nonNull(spaTreatmentService.insertNewSpaTreatment(spaTreatmentInsert))) {
            for (TreatmentService treatmentService : treatmentServices) {
                treatmentService.setSpaTreatment(spaTreatmentInsert);
                TreatmentService treatmentServiceResult =
                        treatmentServiceService.insertNewTreatmentService(treatmentService);
                if (Objects.isNull(treatmentServiceResult)) {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
                    return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
                } else {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.TREATMENT_SERVICE));
                }
            }
            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.TREATMENT_SERVICE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
    }

    @GetMapping("/spaTreatment/findByPackageId")
    public Response findSpaTreatmentByPackageId(@RequestParam Integer packageId,
                                                @RequestParam String search, Pageable pageable) {
        long totalItem = spaTreatmentService.findByPackageId(packageId, Constant.SEARCH_NO_CONTENT,
                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                .getTotalElements();
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(packageId, search, pageable);
        if (!spaTreatments.hasContent() && !spaTreatments.isFirst()) {
            spaTreatments = spaTreatmentService.findByPackageId(packageId, search,
                    PageRequest.of(spaTreatments.getTotalPages() - 1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        List<SpaTreatment> spaTreatmentList = new ArrayList<>();
        List<SpaTreatment> spaTreatmentCheckList = spaTreatments.getContent();
        for (int i = 0; i < spaTreatmentCheckList.size(); i++) {
            SpaTreatment spaTreatmentCheck = spaTreatmentCheckList.get(i);
            List<TreatmentService> treatmentServices =
                    new ArrayList<>(spaTreatmentCheck.getTreatmentServices());
            spaTreatmentCheck.setTreatmentServices(new TreeSet<>());
            Collections.sort(treatmentServices);
            for (TreatmentService treatmentService : treatmentServices) {
                spaTreatmentCheck.getTreatmentServices().add(treatmentService);
            }
            spaTreatmentList.add(spaTreatmentCheck);
        }
        Page<SpaTreatment> pageResult = new PageImpl<>(spaTreatmentList, pageable, totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(pageResult));
    }

    @PutMapping(value = "/spapackage/edit/{packageId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editSpaPackage(@PathVariable Integer packageId,
                                   SpaPackageRequest spaPackage) {
        SpaPackage spaPackageEdit = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackageEdit)) {
            if (Objects.nonNull(spaPackage.getFile())) {
                String imageLink = UploadImage.uploadImage(spaPackage.getFile());
                if (imageLink != "") {
                    spaPackageEdit.setImage(imageLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(spaPackage.getName())) {
                spaPackageEdit.setName(spaPackage.getName());
            }
            if (Objects.nonNull(spaPackage.getDescription())) {
                spaPackageEdit.setDescription(spaPackage.getDescription());
            }
            SpaPackage spaPackageResult = spaPackageService.editBySpaPackageId(spaPackageEdit);
            if (Objects.nonNull(spaPackageResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SPA_PACKAGE));
                return ResponseHelper.ok(spaPackageResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_PACKAGE));
    }

    @PutMapping("/spatreatment/edit/{spaTreatmentId}")
    public Response editSpaTreatment(@PathVariable Integer spaTreatmentId,
                                     @RequestBody SpaTreatmentRequest spaTreatment) {
        SpaTreatment spaTreatmentEdit = spaTreatmentService.findByTreatmentId(spaTreatmentId);
        if (Objects.nonNull(spaTreatmentEdit)) {
            if (Objects.nonNull(spaTreatment.getName())) {
                spaTreatmentEdit.setName(spaTreatment.getName());
            }
            if (Objects.nonNull(spaTreatment.getDescription())) {
                spaTreatmentEdit.setDescription(spaTreatment.getDescription());
            }
            SpaTreatment spaTreatmentResult = spaTreatmentService.editBySpaTreatment(spaTreatmentEdit);
            if (Objects.nonNull(spaTreatmentResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SPA_TREATMENT));
                return ResponseHelper.ok(spaTreatmentResult);
            }
        }
        LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_TREATMENT));
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_TREATMENT));
    }
}

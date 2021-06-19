package swp490.spa.dto.helper;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import swp490.spa.dto.responses.*;
import swp490.spa.entities.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Conversion {

    public Page<CategoryResponse> convertToPageCategoryResponse(Page<Category> categories) {
        List<CategoryResponse> categoryData = categories.getContent().stream()
                .map(category -> new CategoryResponse(category.getId(),
                        category.getName(),
                        category.getIcon(),
                        category.getDescription(),
                        category.getCreateTime(),
                        category.getCreateBy(),
                        category.getStatus(),
                        category.getSpa()))
                .collect(Collectors.toList());

        long totalElements = categories.getTotalElements();
        return new PageImpl<>(categoryData,
                totalElements == 0 ? Pageable.unpaged() : categories.getPageable(),
                totalElements);
    }

    public Page<UserResponse> convertToPageUserResponse(Page<User> users) {
        List<UserResponse> userData = users.getContent().stream()
                .map(user -> new UserResponse(user.getId(),
                        user.getFullname(),
                        user.getPhone(),
                        user.getPassword(),
                        user.getGender(),
                        user.getBirthdate(),
                        user.getEmail(),
                        user.getImage(),
                        user.getAddress(),
                        user.isActive()))
                .collect(Collectors.toList());

        long totalElements = users.getTotalElements();
        return new PageImpl<>(userData,
                totalElements == 0 ? Pageable.unpaged() : users.getPageable(),
                totalElements);
    }

    public Page<SpaResponse> convertToPageSpaResponse(Page<Spa> spas) {
        List<SpaResponse> spaData = spas.getContent().stream()
                .map(spa -> new SpaResponse(spa.getId(),
                        spa.getName(),
                        spa.getImage(),
                        spa.getStreet(),
                        spa.getDistrict(),
                        spa.getCity(),
                        spa.getLatitude(),
                        spa.getLongtitude(),
                        spa.getCreateBy(),
                        spa.getCreateTime(),
                        spa.getStatus()))
                .collect(Collectors.toList());
        long totalElements = spas.getTotalElements();
        return new PageImpl<>(spaData, totalElements == 0 ? Pageable.unpaged() : spas.getPageable(),
                totalElements);
    }

    public Page<SpaServiceResponse> convertToPageSpaServiceResponse(Page<SpaService> spaServices){
        List<SpaServiceResponse> spaServiceData = spaServices.getContent().stream()
                .map(spaService -> new SpaServiceResponse(spaService.getId(),
                        spaService.getName(),
                        spaService.getImage(),
                        spaService.getDescription(),
                        spaService.getPrice(),
                        spaService.getStatus(),
                        spaService.getType(),
                        spaService.getDurationMin(),
                        spaService.getCreateTime(),
                        spaService.getCreateBy(),
                        spaService.getSpa()))
                .collect(Collectors.toList());
        long totalElements = spaServices.getTotalElements();
        return new PageImpl<>(spaServiceData, totalElements == 0 ? Pageable.unpaged() : spaServices.getPageable(),
                totalElements);
    }

    public Page<SpaTreatmentResponse> convertToPageSpaTreatmentResponse(Page<SpaTreatment> spaTreatments){
        List<SpaTreatmentResponse> spaTreatmentData = spaTreatments.getContent().stream()
                .map(spaTreatment -> new SpaTreatmentResponse(spaTreatment.getId(),
                        spaTreatment.getName(),
                        spaTreatment.getDescription(),
                        spaTreatment.getTotalTime(),
                        spaTreatment.getCreateTime(),
                        spaTreatment.getCreateBy(),
                        spaTreatment.getSpaPackage(),
                        spaTreatment.getSpa(),
                        spaTreatment.getTreatmentServices()))
                .collect(Collectors.toList());
        long totalElements = spaTreatments.getTotalElements();
        return new PageImpl<>(spaTreatmentData, totalElements == 0 ? Pageable.unpaged() : spaTreatments.getPageable(),
                totalElements);
    }

    public Page<SpaPackageResponse> convertToPageSpaPackageResponse(Page<SpaPackage> spaPackages){
        List<SpaPackageResponse> spaPackageData = spaPackages.getContent().stream()
                .map(spaPackage -> new SpaPackageResponse(spaPackage.getId(),
                        spaPackage.getName(),
                        spaPackage.getDescription(),
                        spaPackage.getImage(),
                        spaPackage.getType(),
                        spaPackage.getStatus(),
                        spaPackage.getCreateTime(),
                        spaPackage.getCreate_by(),
                        spaPackage.getCategory(),
                        spaPackage.getSpa(),
                        spaPackage.getSpaServices()))
                .collect(Collectors.toList());
        long totalElements = spaPackages.getTotalElements();
        return new PageImpl<>(spaPackageData, totalElements == 0 ? Pageable.unpaged() : spaPackages.getPageable(),
                totalElements);
    }

    public SpaPackageResponse convertToSpaPackageResponse(SpaPackage spaPackage){
        SpaPackageResponse spaPackageResponse = new SpaPackageResponse(spaPackage.getId(),
                spaPackage.getName(),
                spaPackage.getDescription(),
                spaPackage.getImage(),
                spaPackage.getType(),
                spaPackage.getStatus(),
                spaPackage.getCreateTime(),
                spaPackage.getCreate_by(),
                spaPackage.getCategory(),
                spaPackage.getSpa(),
                spaPackage.getSpaServices());
        return spaPackageResponse;
    }

    public Page<SpaPackageTreatmentResponse> convertToPageSpaPackageTreatmentResponse(Page<SpaPackageTreatmentResponse> page) {
        List<SpaPackageTreatmentResponse> spaPackageTreatmentData = page.getContent().stream()
                .map(spaPackageTreatment -> new SpaPackageTreatmentResponse(spaPackageTreatment.getSpaPackage(),
                        spaPackageTreatment.getSpaTreatments()))
                .collect(Collectors.toList());
        long totalElements = page.getTotalElements();
        return new PageImpl<>(spaPackageTreatmentData, totalElements == 0 ? Pageable.unpaged() : page.getPageable(),
                totalElements);
    }
}

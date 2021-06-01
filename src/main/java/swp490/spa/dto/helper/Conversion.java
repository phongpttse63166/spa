package swp490.spa.dto.helper;

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
                        user.getAddress()))
                .collect(Collectors.toList());

        long totalElements = users.getTotalElements();
        return new PageImpl<>(userData,
                totalElements == 0 ? Pageable.unpaged() : users.getPageable(),
                totalElements);
    }

    public Page<SpaResponse> convertToSpaResponse(Page<Spa> spas) {
        List<SpaResponse> spaData = spas.getContent().stream()
                .map(spa -> new SpaResponse(spa.getId(),
                        spa.getName(),
                        spa.getImage(),
                        spa.getCreateBy(),
                        spa.getCreateTime(),
                        spa.getStatus()))
                .collect(Collectors.toList());
        long totalElements = spas.getTotalElements();
        return new PageImpl<>(spaData, totalElements == 0 ? Pageable.unpaged() : spas.getPageable(),
                totalElements);
    }

    public Page<SpaAddressResponse> convertToSpaAddressResponse(Page<SpaAddress> spaAddresses) {
        List<SpaAddressResponse> spaAddressData = spaAddresses.getContent().stream()
                .map(spaAddress -> new SpaAddressResponse(spaAddress.getId(),
                        spaAddress.getStreet(),
                        spaAddress.getDistrict(),
                        spaAddress.getCity(),
                        spaAddress.getSpa()))
                .collect(Collectors.toList());
        long totalElements = spaAddresses.getTotalElements();
        return new PageImpl<>(spaAddressData, totalElements == 0 ? Pageable.unpaged() : spaAddresses.getPageable(),
                totalElements);
    }

    public Page<SpaServiceResponse> convertToSpaServiceResponse(Page<SpaService> spaServices){
        List<SpaServiceResponse> spaServiceData = spaServices.getContent().stream()
                .map(spaService -> new SpaServiceResponse(spaService.getId(),
                        spaService.getName(),
                        spaService.getDescription(),
                        spaService.getPrice(),
                        spaService.getStatus(),
                        spaService.getCreateTime(),
                        spaService.getCreateBy()))
                .collect(Collectors.toList());
        long totalElements = spaServices.getTotalElements();
        return new PageImpl<>(spaServiceData, totalElements == 0 ? Pageable.unpaged() : spaServices.getPageable(),
                totalElements);
    }
}

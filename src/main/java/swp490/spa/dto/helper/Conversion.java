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
    public Page<CategoryResponse> convertToPageCategoryResponse(Page<Category> categories) {
        List<CategoryResponse> categoryData = categories.getContent().stream()
                .map(category -> new CategoryResponse(category.getId(),
                        category.getName(),
                        category.getIcon(),
                        category.getDescription(),
                        category.getCreateTime(),
                        category.getCreateBy(),
                        category.getStatus()))
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
                        spa.getLongitude(),
                        spa.getCreateBy(),
                        spa.getCreateTime(),
                        spa.getStatus()))
                .collect(Collectors.toList());
        long totalElements = spas.getTotalElements();
        return new PageImpl<>(spaData, totalElements == 0 ? Pageable.unpaged() : spas.getPageable(),
                totalElements);
    }

    public Page<SpaServiceResponse> convertToPageSpaServiceResponse(Page<SpaService> spaServices) {
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
                        spaService.getCreateBy()))
                .collect(Collectors.toList());
        long totalElements = spaServices.getTotalElements();
        return new PageImpl<>(spaServiceData, totalElements == 0 ? Pageable.unpaged() : spaServices.getPageable(),
                totalElements);
    }

    public Page<SpaTreatmentResponse> convertToPageSpaTreatmentResponse(Page<SpaTreatment> spaTreatments) {
        List<SpaTreatmentResponse> spaTreatmentData = spaTreatments.getContent().stream()
                .map(spaTreatment -> new SpaTreatmentResponse(spaTreatment.getId(),
                        spaTreatment.getName(),
                        spaTreatment.getDescription(),
                        spaTreatment.getTotalPrice(),
                        spaTreatment.getTotalTime(),
                        spaTreatment.getCreateTime(),
                        spaTreatment.getCreateBy(),
                        spaTreatment.getSpaPackage(),
                        spaTreatment.getTreatmentServices()))
                .collect(Collectors.toList());
        long totalElements = spaTreatments.getTotalElements();
        return new PageImpl<>(spaTreatmentData, totalElements == 0 ? Pageable.unpaged() : spaTreatments.getPageable(),
                totalElements);
    }

    public Page<SpaPackageResponse> convertToPageSpaPackageResponse(Page<SpaPackage> spaPackages) {
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
                        spaPackage.getSpaServices()))
                .collect(Collectors.toList());
        long totalElements = spaPackages.getTotalElements();
        return new PageImpl<>(spaPackageData, totalElements == 0 ? Pageable.unpaged() : spaPackages.getPageable(),
                totalElements);
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

    public Page<BookingResponse> convertToPageBookingResponse(Page<Booking> page) {
        List<BookingResponse> bookingData = page.getContent().stream()
                .map(booking -> new BookingResponse(booking.getId(),
                        booking.getTotalPrice(),
                        booking.getTotalTime(),
                        booking.getStatusBooking(),
                        booking.getCreateTime(),
                        booking.getCustomer(),
                        booking.getSpa(),
                        booking.getBookingDetails().stream()
                                .map(bookingDetail -> new BookingDetailResponse(bookingDetail.getId(),
                                        bookingDetail.getTotalTime(),
                                        bookingDetail.getType(),
                                        bookingDetail.getTotalPrice(),
                                        bookingDetail.getStatusBooking(),
                                        bookingDetail.getBooking(),
                                        bookingDetail.getSpaTreatment(),
                                        bookingDetail.getSpaPackage(),
                                        bookingDetail.getBookingDetailSteps().stream()
                                                .map(bookingDetailStep -> new BookingDetailStepResponse(bookingDetailStep.getId(),
                                                        bookingDetailStep.getDateBooking(),
                                                        bookingDetailStep.getStartTime(),
                                                        bookingDetailStep.getEndTime(),
                                                        bookingDetailStep.getBookingPrice(),
                                                        bookingDetailStep.getStatusBooking(),
                                                        bookingDetailStep.getReason(),
                                                        bookingDetailStep.getIsConsultation(),
                                                        bookingDetailStep.getConsultationContent(),
                                                        bookingDetailStep.getRating(),
                                                        bookingDetailStep.getTreatmentService(),
                                                        bookingDetailStep.getStaff(),
                                                        bookingDetailStep.getConsultant(),
                                                        bookingDetailStep.getBookingDetail())).collect(Collectors.toList())))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        long totalElements = page.getTotalElements();
        return new PageImpl<>(bookingData, totalElements == 0 ? Pageable.unpaged() : page.getPageable(),
                totalElements);
    }

    public Page<BookingDetailResponse> convertToPageBookingDetailResponse(Page<BookingDetail> page) {
        List<BookingDetailResponse> bookingDetailData = page.getContent().stream()
                .map(bookingDetail -> new BookingDetailResponse(bookingDetail.getId(),
                        bookingDetail.getTotalTime(),
                        bookingDetail.getType(),
                        bookingDetail.getTotalPrice(),
                        bookingDetail.getStatusBooking(),
                        bookingDetail.getBooking(),
                        bookingDetail.getSpaTreatment(),
                        bookingDetail.getSpaPackage(),
                        bookingDetail.getBookingDetailSteps().stream()
                                .map(bookingDetailStep -> new BookingDetailStepResponse(bookingDetailStep.getId(),
                                        bookingDetailStep.getDateBooking(),
                                        bookingDetailStep.getStartTime(),
                                        bookingDetailStep.getEndTime(),
                                        bookingDetailStep.getBookingPrice(),
                                        bookingDetailStep.getStatusBooking(),
                                        bookingDetailStep.getReason(),
                                        bookingDetailStep.getIsConsultation(),
                                        bookingDetailStep.getConsultationContent(),
                                        bookingDetailStep.getRating(),
                                        bookingDetailStep.getTreatmentService(),
                                        bookingDetailStep.getStaff(),
                                        bookingDetailStep.getConsultant(),
                                        bookingDetailStep.getBookingDetail())).collect(Collectors.toList())))
                .collect(Collectors.toList());
        long totalElements = page.getTotalElements();
        return new PageImpl<>(bookingDetailData, totalElements == 0 ? Pageable.unpaged() : page.getPageable(),
                totalElements);
    }

    public Page<DateOffResponse> convertToPageDateOffResponse(Page<DateOff> dateOffPage) {
        List<DateOffResponse> dateOffData = dateOffPage.getContent().stream()
                .map(dateOff -> new DateOffResponse(dateOff.getId(),
                        dateOff.getDateOff(),
                        dateOff.getStatusDateOff(),
                        dateOff.getReasonDateOff(),
                        dateOff.getReasonCancel(),
                        dateOff.getManager(),
                        dateOff.getEmployee(),
                        dateOff.getSpa()))
                .collect(Collectors.toList());
        long totalElements = dateOffPage.getTotalElements();
        return new PageImpl<>(dateOffData, totalElements == 0 ? Pageable.unpaged() : dateOffPage.getPageable(),
                totalElements);
    }

    public Page<BookingDetailStepResponse> convertToPageBookingDetailStepResponse(Page<BookingDetailStep> bookingDetailSteps) {
        List<BookingDetailStepResponse> bookingDetailStepData = bookingDetailSteps.getContent().stream()
                .map(bookingDetailStep -> new BookingDetailStepResponse(bookingDetailStep.getId(),
                        bookingDetailStep.getDateBooking(),
                        bookingDetailStep.getStartTime(),
                        bookingDetailStep.getEndTime(),
                        bookingDetailStep.getBookingPrice(),
                        bookingDetailStep.getStatusBooking(),
                        bookingDetailStep.getReason(),
                        bookingDetailStep.getIsConsultation(),
                        bookingDetailStep.getConsultationContent(),
                        bookingDetailStep.getRating(),
                        bookingDetailStep.getTreatmentService(),
                        bookingDetailStep.getStaff(),
                        bookingDetailStep.getConsultant(),
                        bookingDetailStep.getBookingDetail()))
                .collect(Collectors.toList());
        long totalElements = bookingDetailSteps.getTotalElements();
        return new PageImpl<>(bookingDetailStepData,
                totalElements == 0 ? Pageable.unpaged() : bookingDetailSteps.getPageable(),
                totalElements);
    }
}

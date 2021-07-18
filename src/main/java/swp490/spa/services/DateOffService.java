package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.DateOff;
import swp490.spa.entities.StatusDateOff;
import swp490.spa.repositories.DateOffRepository;

import java.sql.Date;
import java.util.List;

@Service
public class DateOffService {
    @Autowired
    DateOffRepository dateOffRepository;

    public DateOff insertNewDateOff(DateOff dateOff) {
        return this.dateOffRepository.save(dateOff);
    }

    public DateOff findDateOffById(Integer dateOffId) {
        return this.dateOffRepository.findById(dateOffId).get();
    }

    public DateOff editDateOff(DateOff dateOff) {
        return this.dateOffRepository.save(dateOff);
    }

    public List<DateOff> findByDateOffAndSpaAndStatusApprove(Date dateOff, Integer spaId) {
        return this.dateOffRepository
                .findByDateOffAndAndSpa_IdAndAndStatusDateOff(dateOff, spaId, StatusDateOff.APPROVE);
    }

    public Page<DateOff> findBySpaAndStatusInRangeDate(Integer spaId, StatusDateOff waiting,
                                                       Date fromDate, Date toDate, Pageable pageable) {
        return this.dateOffRepository
                .findBySpa_IdAndStatusDateOffAndDateOffBetweenOrderByDateOff(spaId, waiting,
                        fromDate, toDate, pageable);
    }

    public void removeDateOff(Integer dateOffId) {
        this.dateOffRepository.deleteById(dateOffId);
    }

    public List<DateOff> findBySpaAndFromToDateAndStatus(Integer spaId, Date startDate,
                                                         Date endDate, StatusDateOff status) {
        return this.dateOffRepository
                .findBySpa_IdAndDateOffBetweenAndStatusDateOffOrderByDateOffAsc(spaId, startDate,
                        endDate, status);
    }
}

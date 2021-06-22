package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.DateOff;
import swp490.spa.repositories.DateOffRepository;

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
}

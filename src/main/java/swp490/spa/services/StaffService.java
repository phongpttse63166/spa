package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Staff;
import swp490.spa.repositories.StaffRepository;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public Staff findByStaffId(Integer userId){
        return this.staffRepository.findByUserId(userId);
    }
}

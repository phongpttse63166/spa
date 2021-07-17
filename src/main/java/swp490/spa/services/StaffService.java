package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Staff;
import swp490.spa.repositories.StaffRepository;

import java.util.List;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public Staff findByStaffId(Integer userId){
        return this.staffRepository.findByUserId(userId);
    }

    public List<Staff> findBySpaId(Integer spaId) {
        return this.staffRepository.findBySpa_Id(spaId);
    }

    public List<Staff> findBySpaIdAndNameLike(Integer spaId, String search) {
        return this.staffRepository.findStaffBySpaIdAndNameLike(spaId, search);
    }

    public Staff editStaff(Staff staff) {
        return this.staffRepository.save(staff);
    }

    public Staff insertNewStaff(Staff staff) {
        return this.staffRepository.save(staff);
    }
}

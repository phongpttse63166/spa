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

    public Page<Staff> findBySpaIdAndNameLike(Integer spaId, String search, Pageable pageable) {
        return this.staffRepository.findStaffBySpaIdAndNameLike(spaId, search, pageable);
    }

    public Staff editStaff(Staff staff) {
        return this.staffRepository.save(staff);
    }
}

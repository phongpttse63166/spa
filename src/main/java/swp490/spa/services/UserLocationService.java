package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.UserLocation;
import swp490.spa.repositories.UserLocationrepository;

@Service
public class UserLocationService {
    @Autowired
    UserLocationrepository userLocationrepository;

    public UserLocation findUserLocationByUserId(Integer userId){
        return this.userLocationrepository.findUseLocationByUserId(userId);
    }

    public UserLocation insertNewUserLocation(UserLocation userLocation){
        return this.userLocationrepository.save(userLocation);
    }
}

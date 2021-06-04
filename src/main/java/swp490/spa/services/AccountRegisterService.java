package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.AccountRegister;
import swp490.spa.repositories.AccountRegisterRepository;

@Service
public class AccountRegisterService {
    @Autowired
    AccountRegisterRepository accountRegisterRepository;

    public AccountRegister insertNewAccountRegister(AccountRegister accountRegister){
        return this.accountRegisterRepository.save(accountRegister);
    }

    public AccountRegister updateAccountRegister(AccountRegister accountRegister){
        return this.accountRegisterRepository.save(accountRegister);
    }

    public void deleteAccountRegister(Integer accountId){
        this.accountRegisterRepository.deleteById(accountId);
    }

    public AccountRegister findByPhone(String phone){
        return this.accountRegisterRepository.findByPhone(phone);
    }
}

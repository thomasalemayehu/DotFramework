package application.customer;


import application.account.AccountRepository;
import application.utils.EmailSender;
import dotFramework.annotations.Autowired;
import dotFramework.annotations.Service;

@Service
public class CustomerRepository {



    public AccountRepository accountRepository;

    public EmailSender emailSender;


    public CustomerRepository(EmailSender emailSender){
        this.emailSender = emailSender;
    }


    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public CustomerRepository(){}
}

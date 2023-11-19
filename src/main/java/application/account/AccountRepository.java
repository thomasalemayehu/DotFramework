package application.account;


import application.customer.CustomerService;
import application.utils.EmailSender;
import dotFramework.annotations.Service;

@Service
public class AccountRepository {

    public EmailSender emailSender = new EmailSender();

    public AccountRepository(){}

    public void save(){
        emailSender.sendEmail();
        System.out.println("Saving Account Info ...");
    }
}

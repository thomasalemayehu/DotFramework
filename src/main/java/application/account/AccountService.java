package application.account;


import application.customer.CustomerService;
import application.utils.Logger;
import dotFramework.annotations.Autowired;
import dotFramework.annotations.Scheduled;
import dotFramework.annotations.Service;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class AccountService {



    public AccountRepository accountRepository;

    @Autowired
    public CustomerService customerService;


    @Autowired
    public Logger logger;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountService(){}



    public void deposit(int amount){
        customerService.getInfo();
        System.out.println("Depositing " + amount + " ...");
        accountRepository.save();
        logger.log("Logging Deposit info...");
    }

    public void withDraw(int amount){
        customerService.getInfo();
        System.out.println("Withdrawing " + amount + " ...");
        accountRepository.save();
        logger.log("Logging Withdraw info...");
    }

    @Scheduled(fixedRate = 5000)
    public void welcome() {
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        String currenttime = timeFormatter.format(date);
        System.out.println("This task runs at " + currenttime);
    }

    @Scheduled(cron = "8 0")
    public void welcome2() {
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        String currenttime = timeFormatter.format(date);
        System.out.println("This cron task runs at " + currenttime);
    }
}

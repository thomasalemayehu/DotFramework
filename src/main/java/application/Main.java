package application;

import application.account.AccountRepository;
import application.account.AccountService;
import application.customer.CustomerRepository;
import application.customer.CustomerService;
import application.utils.EmailSender;
import application.utils.Logger;
import dotFramework.annotations.Autowired;
import dotFramework.annotations.Value;
import dotFramework.context.DotFramework;

public class Main implements Runnable {

    @Value(name="bankname")
    String bankName;


    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {
        DotFramework.run(Main.class);
    }

    @Override
    public void run() {
        System.out.println("Bank " + bankName + " starting...");
        accountService.deposit(30);
        accountService.withDraw(10);
    }
}

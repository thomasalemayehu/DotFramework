package application.utils;


import dotFramework.annotations.Service;

@Service
public class EmailSender {


    public EmailSender(){}
    public void sendEmail(){
        System.out.println("Sending Email");
    }
}

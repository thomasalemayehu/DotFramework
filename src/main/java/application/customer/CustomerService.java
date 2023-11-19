package application.customer;


import dotFramework.annotations.Service;

@Service
public class CustomerService {


    public CustomerService(){}

    public void getInfo(){
        System.out.println("Getting Customer Info");
    }
}

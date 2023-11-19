package application.utils;


import dotFramework.annotations.Service;

@Service
public class Logger {

    public Logger(){}
    public void log(String message){
        System.out.println(message);
    }
}

package com.packtub.ge.hello;

/**
 * Created by jm186111 on 05/04/2016.
 */
public class Main {
    public static void main(String[] args){
        GreetingService service = new GreetingService();
        System.out.println(service.greet("Jorge"));
    }
}

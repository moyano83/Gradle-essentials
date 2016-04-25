package com.packtub.ge.hello;

public class GreetingService {

    public synchronized String greet(String user) {
        return "Hello " + user;
    }
}
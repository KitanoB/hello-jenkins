package com.ktp.hello;
public class HelloService {
    public String greet(String name) {
        if (name == null || name.isBlank()) return "Hello, world!";
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println(new HelloService().greet(args.length > 0 ? args[0] : null));
    }
}
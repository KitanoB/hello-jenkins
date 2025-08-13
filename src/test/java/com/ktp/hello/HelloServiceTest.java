package com.ktp.hello;

import com.ktp.hello.HelloService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HelloServiceTest {

    @Test
    public void greet_withName() {
        var svc = new HelloService();
        assertEquals("Hello, KTP!", svc.greet("KTP"));
    }

    @Test
    public void greet_nullOrBlank() {
        var svc = new HelloService();
        assertEquals("Hello, world!", svc.greet(null));
        assertEquals("Hello, world!", svc.greet("  "));
    }
}
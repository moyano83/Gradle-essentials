package com.packtub.ge.hello;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by jm186111 on 05/04/2016.
 */
public class GreetingServiceTest {

    private GreetingService service = new GreetingService();

    @Test
    public void testGreeting(){
        TestCase.assertEquals(service.greet("Test"), "Hello Test");
    }
}

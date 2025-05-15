package com.hiutin.awcsquy.container;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestContainer {
    // Test endpoint to check if the container is running
    @GetMapping("/test")
    public String test() {
        return "Container is running!";
    }

    // Add more endpoints or methods as needed for testing
}

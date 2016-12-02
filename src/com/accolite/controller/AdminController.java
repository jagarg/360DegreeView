package com.accolite.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class AdminController {

	@RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World! Welcome to ERD Visualizer ... ";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AdminController.class, args);
    }
}

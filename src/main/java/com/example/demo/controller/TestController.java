package com.example.demo.controller;
import com.example.demo.dao.MyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@Slf4j
@RestController
public class TestController {

    @Resource
    private MyClient client;

    @GetMapping("/hello")
    public String test() {
        return "hello";
    }

    @GetMapping("/test-hello")
    public String testHello() {
        String s = client.simpleRequest();
        log.info("s==="+s);
        return s;
    }



}

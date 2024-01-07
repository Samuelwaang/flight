package com.travel.flight;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // ready for spring to use to handle web requests
public class MainController {
  @GetMapping("/")
  public String index() {
    return "hi";
  }
}

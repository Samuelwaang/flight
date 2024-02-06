package com.travel.flight;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // ready for spring to use to handle web requests
public class MainController {
  @GetMapping("/")
  public String index() {
    return "d";
  }
  @GetMapping("/test")
  public String test() {
    return "tet";
  }

}

package pl.petgo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @GetMapping("/api/hello")
    public String sampleText(){
        return "Hello from Petgo API!";
    }
}

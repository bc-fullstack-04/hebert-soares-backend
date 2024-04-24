package br.com.sysmap.bootcamp.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ping", description = "Test API")
@RequestMapping("/ping")
public class PingController {

    @Operation(summary = "Test ping endpoint")
    @GetMapping
    public String ping() {
        return "pong";
    }
}

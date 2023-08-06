package com.bitnine.logcollector.api;

import com.bitnine.logcollector.service.LogCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log-collector/v1")
public class LogCollectorController {

    private final LogCollectorService logCollectorService;

    @PostMapping("/add")
    public ResponseEntity<String> serviceStart(){
        logCollectorService.serviceStart();
        return ResponseEntity.ok("service 시작");
    }
}

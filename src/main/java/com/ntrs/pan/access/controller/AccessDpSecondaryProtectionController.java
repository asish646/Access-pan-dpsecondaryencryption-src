package com.ntrs.pan.access.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntrs.pan.access.request.ProtectModel;
import com.ntrs.pan.access.service.AccessDpSecondaryProtectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AccessDpSecondaryProtectionController {

    private final AccessDpSecondaryProtectionService service;
    private final ObjectMapper objectMapper;

    public AccessDpSecondaryProtectionController(AccessDpSecondaryProtectionService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/protect")
    public ResponseEntity<Resource> getProtectionResponse(@RequestParam(value = "jsonRequest", required = true) String jsonRequest,
                                                          @RequestPart(value = "file", required = true) MultipartFile file) throws IOException {

        byte[] response= null;
        ProtectModel request = objectMapper.readValue(jsonRequest, ProtectModel.class);

        if(file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ByteArrayResource("File must not be empty. Please provide a valid file.".getBytes()));
        }

        try {
//            response = service.accessProtectionAzureFunction(request, file);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Upstream processing error. ".getBytes()));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=downloaded-file");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Result file empty. Check input file. ".getBytes()));
        } else return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new ByteArrayResource(response));
    }
}

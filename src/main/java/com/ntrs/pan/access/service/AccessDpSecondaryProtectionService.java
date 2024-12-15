package com.ntrs.pan.access.service;

import com.ntrs.pan.access.request.ProtectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AccessDpSecondaryProtectionService {

    @Value("${azureFunction.protect.url}")
    private String protectUrl;

    @Autowired
    private RestTemplate restTemplate;

    public byte[] accessProtectionAzureFunction(ProtectModel request, MultipartFile file) throws IOException {

        // Create a MultiValueMap to hold both the JSON string and the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("jsonRequest", request);
        // Add the file as a part (MultipartFile is converted into Resource)
        byte[] fileBytes = file.getBytes(); // get file bytes
        Resource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(protectUrl, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCodeValue() == 200) return response.getBody();
        else return null;
    }
}

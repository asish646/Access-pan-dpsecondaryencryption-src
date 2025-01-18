package com.ntrs.pan.access.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntrs.pan.access.request.InputRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AccessDpSecondaryProtectionService {

    @Value("${azureFunction.protect.url}")
    private String protectUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void accessProtectionAzureFunction() throws IOException {

        String inputJson = new String(Files.readAllBytes(new ClassPathResource("input.json").getFile().toPath()));
        InputRequest request = objectMapper.readValue(inputJson, InputRequest.class);

        Path path = Paths.get(request.getInputFileLocation());

        // Create a MultiValueMap to hold both the JSON string and the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("jsonRequest", request);
        // Add the file as a part (MultipartFile is converted into Resource)
        byte[] fileBytes = Files.readAllBytes(path); // get file bytes
        Resource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return request.getInputFileLocation();
            }
        };
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(protectUrl, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCodeValue() == 200) {

            Path dirPath = Paths.get(request.getOutputFileDir());
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);  // Create the directory if it doesn't exist
            }
            // Create a new file path inside the specified directory
            Path filePath = dirPath.resolve(path.getFileName());

            // Write the byte array to the new file
            Files.write(filePath, response.getBody());
            System.out.println("File written to: " + filePath.toString());
        } else {
            System.out.println("Result file empty. Check input file. ");
        }
    }
}

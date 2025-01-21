package com.ntrs.pan.access.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntrs.pan.access.request.InputRequest;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("jsonRequest", request);
        // Add the file as a part (MultipartFile is converted into Resource)
//        byte[] fileBytes = Files.readAllBytes(path); // get file bytes
//        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
//            @Override
//            public String getFilename() {
//                return request.getInputFileLocation();
//            }
//        };
//        body.add("file", fileResource);

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("jsonRequest", inputJson)
                .addFormDataPart("file", path.getFileName().toString(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), new File(request.getInputFileLocation()))
                ).build();

        Request httpRequest = new Request.Builder()
                .url(protectUrl)
                .method("POST", body)
                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Inp4ZWcyV09OcFRrd041R21lWWN1VGR0QzZKMCJ9.eyJhdWQiOiIzZmRiNWI0My0yMTk2LTRhODAtOGNiMi04NTdiYjJiNGEzMzkiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vMjQzNDUyOGQtNDI3MC00OTc3LTgxZGQtYTYzMDhjMTc2MWEzL3YyLjAiLCJpYXQiOjE3MzM0NTMxNzAsIm5iZiI6MTczMzQ1MzE3MCwiZXhwIjoxNzMzNDU3MDcwLCJhaW8iOiJrMkJnWVBqTzhhMnNUOGR6MFE0elppT0hrN21zK2pZL0xoeCt3VERkM3YxdlFFdVFXendBIiwiYXpwIjoiM2ZkYjViNDMtMjE5Ni00YTgwLThjYjItODU3YmIyYjRhMzM5IiwiYXpwYWNyIjoiMSIsIm9pZCI6IjIzNTBiMzJkLTk1NDYtNDdhZS1hYmU3LTI2Yjg0NDhkNDE0YSIsInJoIjoiMS5BUzBBalZJMEpIQkNkMG1CM2FZd2pCZGhvME5iMnotV0lZQktqTEtGZTdLMG96a3RBQUF0QUEuIiwic3ViIjoiMjM1MGIzMmQtOTU0Ni00N2FlLWFiZTctMjZiODQ0OGQ0MTRhIiwidGlkIjoiMjQzNDUyOGQtNDI3MC00OTc3LTgxZGQtYTYzMDhjMTc2MWEzIiwidXRpIjoiUFJQbXBKbmNPRVM3Y3o3TFB6b0tBQSIsInZlciI6IjIuMCJ9.NKTChuXNHzc03sGZJyGjwPJ0t6sQK0rZzu9vy2bQln2Iq2eZny9AeKxmrlHfR2COQHnsUOSX7MymE6in9fQXeJdPKxWLojMiQm5n_ZF_iFcGO69wcKcKVAAW2CCMYML-DJm1CxkBzoX3iz5nqXjlf_kHvoRaGKucLTmsF1nxfbbtBP8tLvlea-j2xvPXeZ6EUi6nIv-fLm4kvDeSWXJsgmrH1f2LfrT-ZULCdlr6KYywEQPaGipF-xpTWkq8cR61obU_1lfw0AIXQi5XkkmEt2uyZR1ERtVdL3q323CgQpxD6cI33lzwLZs_4V2Iu6nDk1qOdxo7wWFx_4JTI65MNA")
                .build();
        Response response = client.newCall(httpRequest).execute();

//        ResponseEntity<byte[]> response = restTemplate.exchange(protectUrl, HttpMethod.POST, requestEntity, byte[].class);

        if (response.code() == 200) {

            Path dirPath = Paths.get(request.getOutputFileDir());
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);  // Create the directory if it doesn't exist
            }
            // Create a new file path inside the specified directory
            Path filePath = dirPath.resolve(path.getFileName());

            // Write the byte array to the new file
            Files.write(filePath, response.body().bytes());
            System.out.println("File written to: " + filePath.toString());
        } else {
            System.out.println("Result file empty. Check input file. ");
        }
    }
}

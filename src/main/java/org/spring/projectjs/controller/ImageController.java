package org.spring.projectjs.controller;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

    @RestController
    public class ImageController {
        @GetMapping("/image-to-base64.do")
        public Map<String,String> convertImageToBase64(
                @RequestParam("imageName") String imageName) {
            String encodedString = null;
            Map<String, String> map = new HashMap<>();

            try {
                String uploadDir = ResourceUtils
                        .getFile("classpath:static/").toPath().toString();
                System.out.println("물리적경로:"+uploadDir);
                String imagePath = uploadDir + File.separator + imageName;

                File file = new File(imagePath);

                byte[] fileContent = Files.readAllBytes(file.toPath());
                encodedString = Base64.getEncoder()
                        .encodeToString(fileContent);
                System.out.println(encodedString);

                String flaskUrl = "http://127.0.0.1:5000/getDecodeImage.fk"
                        +"?imageName=" + imageName;
                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.postForObject(flaskUrl, encodedString, String.class);

                System.out.println("Flask 응답: "+response);
                response = response.replace("\\", "").replace("\n", "");
                map.put("result", "success");
                map.put("imageName", imageName);
                map.put("Flask", response);

            }
            catch (IOException e) {
                e.printStackTrace();
                encodedString = "Error: " + e.getMessage();
                map.put("result", "error");
            }
            return map;
        }
}

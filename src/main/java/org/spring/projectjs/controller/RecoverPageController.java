// src/main/java/org/spring/projectjs/controller/RecoverPageController.java
package org.spring.projectjs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecoverPageController {

    @GetMapping("/find-id.do")
    public String findIdPage() {
        return "recover/find-id";
    }

    @GetMapping("/reset-password.do")
    public String resetPasswordPage() {
        return "recover/reset-password";
    }
}

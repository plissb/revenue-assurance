package com.quantum.ra.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для главной страницы
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Перенаправляем на URL, который обрабатывается FileViewController
        return "redirect:/files";
    }
}
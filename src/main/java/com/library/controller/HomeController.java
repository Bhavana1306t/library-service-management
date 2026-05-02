package com.library.controller;

import com.library.service.AuthorService;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the home/dashboard page ("/").
 */
@Controller
public class HomeController {

    private final BookService   bookService;
    private final AuthorService authorService;

    @Autowired
    public HomeController(BookService bookService, AuthorService authorService) {
        this.bookService   = bookService;
        this.authorService = authorService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Pass live counts to the dashboard stat cards
        model.addAttribute("totalBooks",   bookService.getAllBooks().size());
        model.addAttribute("totalAuthors", authorService.getAllAuthors().size());
        return "index";
    }
}

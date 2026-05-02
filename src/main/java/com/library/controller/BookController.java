package com.library.controller;

import com.library.entity.Book;
import com.library.service.AuthorService;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * MVC Controller for Book CRUD operations.
 *
 * URL mappings (all under /books):
 *   GET  /books            → list all books (uses INNER JOIN query via service)
 *   GET  /books/new        → show add-book form
 *   POST /books/save       → handle add form submission  (CREATE)
 *   GET  /books/edit/{id}  → show edit form
 *   POST /books/update/{id}→ handle update submission   (UPDATE)
 *
 * Note on authorId:
 *   The Author relationship cannot be bound via @ModelAttribute because
 *   the form sends only the author's ID, not a nested Author object.
 *   We therefore accept it as a separate @RequestParam and look up the
 *   Author in the service layer.
 */
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService   bookService;
    private final AuthorService authorService;

    @Autowired
    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService   = bookService;
        this.authorService = authorService;
    }

    // ------------------------------------------------------------------ LIST
    /**
     * Fetches books via the custom INNER JOIN JPQL query.
     * The view receives List<BookAuthorDTO> — no lazy-loading issues.
     */
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooksWithAuthorDetails());
        return "books/list";
    }

    // --------------------------------------------------------- SHOW ADD FORM
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("book",      new Book());
        model.addAttribute("authors",   authorService.getAllAuthors());
        model.addAttribute("formTitle", "Add New Book");
        return "books/form";
    }

    // ------------------------------------------------ HANDLE ADD SUBMISSION
    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book,
                           BindingResult result,
                           @RequestParam("authorId") Long authorId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("authors",   authorService.getAllAuthors());
            model.addAttribute("formTitle", "Add New Book");
            model.addAttribute("selectedAuthorId", authorId);
            return "books/form";
        }
        try {
            authorService.getAuthorById(authorId).ifPresent(book::setAuthor);
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("successMessage",
                "Book '" + book.getTitle() + "' added successfully!");
            return "redirect:/books";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage",     e.getMessage());
            model.addAttribute("authors",          authorService.getAllAuthors());
            model.addAttribute("formTitle",        "Add New Book");
            model.addAttribute("selectedAuthorId", authorId);
            return "books/form";
        }
    }

    // ------------------------------------------------------- SHOW EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        model.addAttribute("book",      book.get());
        model.addAttribute("authors",   authorService.getAllAuthors());
        model.addAttribute("formTitle", "Edit Book");
        return "books/form";
    }

    // ---------------------------------------------- HANDLE UPDATE SUBMISSION
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("book") Book book,
                             BindingResult result,
                             @RequestParam("authorId") Long authorId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            book.setId(id);
            model.addAttribute("authors",          authorService.getAllAuthors());
            model.addAttribute("formTitle",        "Edit Book");
            model.addAttribute("selectedAuthorId", authorId);
            return "books/form";
        }
        try {
            bookService.updateBook(id, book, authorId);
            redirectAttributes.addFlashAttribute("successMessage",
                "Book updated successfully!");
            return "redirect:/books";
        } catch (DataIntegrityViolationException e) {
            book.setId(id);
            model.addAttribute("errorMessage",     e.getMessage());
            model.addAttribute("authors",          authorService.getAllAuthors());
            model.addAttribute("formTitle",        "Edit Book");
            model.addAttribute("selectedAuthorId", authorId);
            return "books/form";
        }
    }
}

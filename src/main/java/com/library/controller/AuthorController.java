package com.library.controller;

import com.library.entity.Author;
import com.library.service.AuthorService;
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
 * MVC Controller for Author CRUD operations.
 *
 * URL mappings (all under /authors):
 *   GET  /authors          → list all authors
 *   GET  /authors/new      → show add-author form
 *   POST /authors/save     → handle add form submission  (CREATE)
 *   GET  /authors/edit/{id}→ show edit form
 *   POST /authors/update/{id} → handle update form submission (UPDATE)
 *   GET  /authors/{id}     → view single author detail
 *
 * @ModelAttribute + BindingResult handle Bean Validation automatically.
 * RedirectAttributes.addFlashAttribute() passes one-time messages across
 * the post-redirect-get (PRG) pattern so they appear after the redirect.
 */
@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ------------------------------------------------------------------ LIST
    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors",          authorService.getAllAuthors());
        model.addAttribute("authorBookCounts", authorService.getAuthorBookCounts());
        return "authors/list";
    }

    // --------------------------------------------------------- SHOW ADD FORM
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("author",    new Author());
        model.addAttribute("formTitle", "Add New Author");
        return "authors/form";
    }

    // ------------------------------------------------ HANDLE ADD SUBMISSION
    /**
     * @Valid triggers Bean Validation on the bound Author object.
     * If there are field errors, BindingResult has them and we
     * re-render the form with error messages — no redirect.
     */
    @PostMapping("/save")
    public String saveAuthor(@Valid @ModelAttribute("author") Author author,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Add New Author");
            return "authors/form";
        }
        try {
            authorService.saveAuthor(author);
            redirectAttributes.addFlashAttribute("successMessage",
                "Author '" + author.getName() + "' added successfully!");
            return "redirect:/authors";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("formTitle", "Add New Author");
            return "authors/form";
        }
    }

    // ------------------------------------------------------- SHOW EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Optional<Author> author = authorService.getAuthorById(id);
        if (author.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Author not found.");
            return "redirect:/authors";
        }
        model.addAttribute("author",    author.get());
        model.addAttribute("formTitle", "Edit Author");
        return "authors/form";
    }

    // ---------------------------------------------- HANDLE UPDATE SUBMISSION
    @PostMapping("/update/{id}")
    public String updateAuthor(@PathVariable Long id,
                               @Valid @ModelAttribute("author") Author author,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Preserve the id so the form action URL stays correct
            author.setId(id);
            model.addAttribute("formTitle", "Edit Author");
            return "authors/form";
        }
        try {
            authorService.updateAuthor(id, author);
            redirectAttributes.addFlashAttribute("successMessage",
                "Author updated successfully!");
            return "redirect:/authors";
        } catch (DataIntegrityViolationException e) {
            author.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("formTitle", "Edit Author");
            return "authors/form";
        }
    }

    // ------------------------------------------------------- VIEW DETAIL
    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<Author> author = authorService.getAuthorById(id);
        if (author.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Author not found.");
            return "redirect:/authors";
        }
        model.addAttribute("author", author.get());
        return "authors/detail";
    }
}

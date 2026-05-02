package com.library.service;

import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Author-related business logic.
 *
 * Responsibilities:
 *   • Wrap repository calls so the controller never talks to JPA directly.
 *   • Enforce business rules (e.g., no duplicate author names).
 *   • Handle exceptions before they bubble up to the controller.
 *
 * @Transactional ensures each public method runs inside a DB transaction.
 * Read-only operations use readOnly=true for a slight performance boost.
 */
@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // ---- READ operations ----

    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Author> searchByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * INNER JOIN query result – only authors who have at least one book.
     */
    @Transactional(readOnly = true)
    public List<Author> getAuthorsWithBooks() {
        return authorRepository.findAuthorsWithBooks();
    }

    /**
     * Returns [authorName, bookCount] pairs ordered by count DESC.
     * Used on the author list page to display the book-count badge.
     */
    @Transactional(readOnly = true)
    public List<Object[]> getAuthorBookCounts() {
        return authorRepository.findAuthorBookCounts();
    }

    // ---- WRITE operations ----

    /**
     * Save (create) a new author.
     * Business rule: author names must be unique (case-insensitive).
     *
     * @throws DataIntegrityViolationException if a duplicate name is detected.
     */
    public Author saveAuthor(Author author) {
        Optional<Author> existing = authorRepository.findByNameIgnoreCase(author.getName());
        if (existing.isPresent() && !existing.get().getId().equals(author.getId())) {
            throw new DataIntegrityViolationException(
                "An author named '" + author.getName() + "' already exists.");
        }
        return authorRepository.save(author);
    }

    /**
     * Update an existing author's fields.
     * Fetches the managed entity by ID, updates fields, then saves — this
     * pattern avoids detached entity issues and keeps the ID intact.
     *
     * @throws IllegalArgumentException if no author with the given ID exists.
     * @throws DataIntegrityViolationException if the new name conflicts.
     */
    public Author updateAuthor(Long id, Author updatedAuthor) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + id));

        // Check for name conflicts excluding the current record
        Optional<Author> nameConflict = authorRepository.findByNameIgnoreCase(updatedAuthor.getName());
        if (nameConflict.isPresent() && !nameConflict.get().getId().equals(id)) {
            throw new DataIntegrityViolationException(
                "An author named '" + updatedAuthor.getName() + "' already exists.");
        }

        existing.setName(updatedAuthor.getName());
        existing.setNationality(updatedAuthor.getNationality());
        existing.setBiography(updatedAuthor.getBiography());
        return authorRepository.save(existing);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
}

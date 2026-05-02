package com.library.service;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthorDTO;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Book-related business logic.
 *
 * Key responsibility: expose the custom INNER JOIN query result
 * (getAllBooksWithAuthorDetails) to the controller so the JSP view
 * never needs to navigate lazy-loaded associations.
 */
@Service
@Transactional
public class BookService {

    private final BookRepository   bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository   = bookRepository;
        this.authorRepository = authorRepository;
    }

    // ---- READ operations ----

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Returns DTO list populated by the INNER JOIN JPQL query.
     * This is what the "List Books" page consumes — no lazy-load risk.
     */
    @Transactional(readOnly = true)
    public List<BookAuthorDTO> getAllBooksWithAuthorDetails() {
        return bookRepository.findAllBooksWithAuthorDetails();
    }

    @Transactional(readOnly = true)
    public List<BookAuthorDTO> getBooksByGenreWithAuthorDetails(String genre) {
        return bookRepository.findBooksByGenreWithAuthorDetails(genre);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // ---- WRITE operations ----

    /**
     * Save (create) a new book.
     * Business rule: ISBN must be globally unique.
     *
     * @throws DataIntegrityViolationException if the ISBN already exists.
     */
    public Book saveBook(Book book) {
        Optional<Book> existing = bookRepository.findByIsbn(book.getIsbn());
        if (existing.isPresent() && !existing.get().getId().equals(book.getId())) {
            throw new DataIntegrityViolationException(
                "A book with ISBN '" + book.getIsbn() + "' already exists.");
        }
        return bookRepository.save(book);
    }

    /**
     * Update an existing book.
     * Accepts the authorId separately because the form submits it as a
     * plain request parameter (not nested inside the Book model object).
     *
     * @throws IllegalArgumentException if the book or author ID is not found.
     * @throws DataIntegrityViolationException if the new ISBN conflicts.
     */
    public Book updateBook(Long id, Book updatedBook, Long authorId) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + authorId));

        // ISBN uniqueness check — exclude the current book from the check
        Optional<Book> isbnConflict = bookRepository.findByIsbn(updatedBook.getIsbn());
        if (isbnConflict.isPresent() && !isbnConflict.get().getId().equals(id)) {
            throw new DataIntegrityViolationException(
                "A book with ISBN '" + updatedBook.getIsbn() + "' already exists.");
        }

        existing.setTitle(updatedBook.getTitle());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setGenre(updatedBook.getGenre());
        existing.setPublicationYear(updatedBook.getPublicationYear());
        existing.setAuthor(author);
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

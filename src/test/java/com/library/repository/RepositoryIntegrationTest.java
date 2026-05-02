package com.library.repository;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BookRepository and AuthorRepository.
 *
 * @DataJpaTest spins up an in-memory H2 database, configures Spring Data JPA,
 * and rolls back each test in isolation — no full Spring context needed.
 * TestEntityManager is a test-specific wrapper around EntityManager that
 * provides persist/flush/find helpers without going through the repository.
 */
@DataJpaTest
@DisplayName("Repository Integration Tests (BookRepository & AuthorRepository)")
class RepositoryIntegrationTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private AuthorRepository  authorRepository;
    @Autowired private BookRepository    bookRepository;

    private Author author1;
    private Author author2;
    private Book   book1;
    private Book   book2;
    private Book   book3;

    /** Seed test data before each test method. */
    @BeforeEach
    void setUp() {
        author1 = entityManager.persist(new Author("George Orwell",   "British", "English novelist"));
        author2 = entityManager.persist(new Author("J.K. Rowling",    "British", "Harry Potter author"));

        book1 = entityManager.persist(new Book("1984",         "ISBN-001", "Dystopian", 1949, author1));
        book2 = entityManager.persist(new Book("Animal Farm",  "ISBN-002", "Satire",    1945, author1));
        book3 = entityManager.persist(new Book("Harry Potter", "ISBN-003", "Fantasy",   1997, author2));

        entityManager.flush();   // ensure writes are visible to the queries below
    }

    // ==================================================================
    //  AuthorRepository Tests
    // ==================================================================

    @Test
    @DisplayName("findByNameIgnoreCase – returns correct author")
    void testFindByNameIgnoreCase() {
        Optional<Author> result = authorRepository.findByNameIgnoreCase("george orwell");
        assertTrue(result.isPresent(), "Author should be found");
        assertEquals("George Orwell", result.get().getName());
    }

    @Test
    @DisplayName("findByNameIgnoreCase – returns empty for unknown name")
    void testFindByNameIgnoreCaseNotFound() {
        Optional<Author> result = authorRepository.findByNameIgnoreCase("Nobody Here");
        assertTrue(result.isEmpty(), "Unknown author should return empty");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase – partial match")
    void testFindByNameContaining() {
        List<Author> results = authorRepository.findByNameContainingIgnoreCase("row");
        assertEquals(1, results.size());
        assertEquals("J.K. Rowling", results.get(0).getName());
    }

    @Test
    @DisplayName("findByNationality – filters by nationality")
    void testFindByNationality() {
        List<Author> britishAuthors = authorRepository.findByNationality("British");
        assertEquals(2, britishAuthors.size(), "Both test authors are British");
    }

    @Test
    @DisplayName("findAuthorsWithBooks (INNER JOIN) – excludes authors without books")
    void testFindAuthorsWithBooks() {
        // Add a third author with no books
        entityManager.persist(new Author("Unknown Author", "Unknown", "No books"));
        entityManager.flush();

        List<Author> authorsWithBooks = authorRepository.findAuthorsWithBooks();

        // Only author1 and author2 have books; Unknown Author must be excluded
        assertEquals(2, authorsWithBooks.size(),
            "INNER JOIN should exclude authors with no books");
    }

    @Test
    @DisplayName("findAuthorBookCounts – correct counts and ordering")
    void testFindAuthorBookCounts() {
        List<Object[]> counts = authorRepository.findAuthorBookCounts();

        assertFalse(counts.isEmpty());
        // George Orwell has 2 books → should appear first (ORDER BY count DESC)
        Object[] top = counts.get(0);
        assertEquals("George Orwell", top[0], "Most prolific author should be first");
        assertEquals(2L, top[1], "George Orwell should have 2 books");
    }

    // ==================================================================
    //  BookRepository Tests
    // ==================================================================

    @Test
    @DisplayName("findByIsbn – returns correct book")
    void testFindByIsbn() {
        Optional<Book> result = bookRepository.findByIsbn("ISBN-001");
        assertTrue(result.isPresent());
        assertEquals("1984", result.get().getTitle());
    }

    @Test
    @DisplayName("findByIsbn – returns empty for non-existent ISBN")
    void testFindByIsbnNotFound() {
        assertTrue(bookRepository.findByIsbn("DOES-NOT-EXIST").isEmpty());
    }

    @Test
    @DisplayName("findByAuthorId – returns all books for author")
    void testFindByAuthorId() {
        List<Book> orwellBooks = bookRepository.findByAuthorId(author1.getId());
        assertEquals(2, orwellBooks.size(), "Orwell should have 2 books");
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase – keyword search")
    void testFindByTitleContaining() {
        List<Book> results = bookRepository.findByTitleContainingIgnoreCase("harry");
        assertEquals(1, results.size());
        assertEquals("Harry Potter", results.get(0).getTitle());
    }

    @Test
    @DisplayName("findByGenreIgnoreCase – genre filter")
    void testFindByGenre() {
        List<Book> dystopianBooks = bookRepository.findByGenreIgnoreCase("dystopian");
        assertEquals(1, dystopianBooks.size());
        assertEquals("1984", dystopianBooks.get(0).getTitle());
    }

    @Test
    @DisplayName("findAllBooksWithAuthorDetails (INNER JOIN) – returns correct DTOs")
    void testFindAllBooksWithAuthorDetails() {
        List<BookAuthorDTO> dtos = bookRepository.findAllBooksWithAuthorDetails();

        assertEquals(3, dtos.size(), "All 3 test books should appear");

        // Verify DTO fields are correctly populated from the JOIN
        BookAuthorDTO orwell1984 = dtos.stream()
                .filter(d -> "1984".equals(d.getBookTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("1984 not found in DTO list"));

        assertEquals("George Orwell", orwell1984.getAuthorName());
        assertEquals("British",       orwell1984.getNationality());
        assertEquals("ISBN-001",      orwell1984.getIsbn());
        assertEquals("Dystopian",     orwell1984.getGenre());
        assertEquals(1949,            orwell1984.getPublicationYear());
    }

    @Test
    @DisplayName("findBooksByGenreWithAuthorDetails – filtered JOIN result")
    void testFindBooksByGenreWithAuthorDetails() {
        List<BookAuthorDTO> results = bookRepository.findBooksByGenreWithAuthorDetails("Fantasy");
        assertEquals(1, results.size());
        assertEquals("Harry Potter",  results.get(0).getBookTitle());
        assertEquals("J.K. Rowling",  results.get(0).getAuthorName());
    }

    @Test
    @DisplayName("findByPublicationYearBetween – year range filter")
    void testFindByPublicationYearBetween() {
        // book1=1949, book2=1945 are in range 1940–1950; book3=1997 is not
        List<Book> results = bookRepository.findByPublicationYearBetween(1940, 1950);
        assertEquals(2, results.size(), "Only 1984 and Animal Farm should be in 1940–1950");
    }
}

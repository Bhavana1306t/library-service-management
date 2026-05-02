package com.library.service;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthorDTO;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService using Mockito.
 *
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations.
 * @Mock creates a mock for the repository.
 * @InjectMocks creates a BookService and injects the mocks automatically.
 *
 * No Spring context, no database — pure unit tests.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock private BookRepository   bookRepository;
    @Mock private AuthorRepository authorRepository;

    @InjectMocks private BookService bookService;

    private Author author;
    private Book   book1;
    private Book   book2;

    @BeforeEach
    void setUp() {
        author = new Author("George Orwell", "British", "English novelist");
        author.setId(1L);

        book1 = new Book("1984", "978-0-452-28423-4", "Dystopian Fiction", 1949, author);
        book1.setId(1L);

        book2 = new Book("Animal Farm", "978-0-451-52634-2", "Political Satire", 1945, author);
        book2.setId(2L);
    }

    // ---- getAllBooks ----

    @Test
    @DisplayName("getAllBooks – returns full list")
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    // ---- getBookById ----

    @Test
    @DisplayName("getBookById – returns book when found")
    void testGetBookByIdFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("1984", result.get().getTitle());
    }

    @Test
    @DisplayName("getBookById – returns empty when not found")
    void testGetBookByIdNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(bookService.getBookById(99L).isEmpty());
    }

    // ---- saveBook ----

    @Test
    @DisplayName("saveBook – saves new book successfully")
    void testSaveBookSuccess() {
        Book newBook = new Book("Brave New World", "978-0-060-92987-7", "Dystopian", 1932, author);
        when(bookRepository.findByIsbn("978-0-060-92987-7")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        Book saved = bookService.saveBook(newBook);

        assertNotNull(saved);
        assertEquals("Brave New World", saved.getTitle());
        verify(bookRepository).save(newBook);
    }

    @Test
    @DisplayName("saveBook – throws exception for duplicate ISBN")
    void testSaveBookDuplicateIsbn() {
        Book duplicate = new Book("Another Book", "978-0-452-28423-4", "Fiction", 2000, author);
        when(bookRepository.findByIsbn("978-0-452-28423-4")).thenReturn(Optional.of(book1));

        assertThrows(DataIntegrityViolationException.class,
                () -> bookService.saveBook(duplicate),
                "Service should throw on duplicate ISBN");

        verify(bookRepository, never()).save(any());
    }

    // ---- updateBook ----

    @Test
    @DisplayName("updateBook – updates all fields correctly")
    void testUpdateBook() {
        Book updates = new Book("1984 — Revised Edition", "978-0-452-28423-4", "Literary Fiction", 1949, null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        // Same ISBN conflict check — but it belongs to THIS book, so no conflict
        when(bookRepository.findByIsbn("978-0-452-28423-4")).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.updateBook(1L, updates, 1L);

        assertEquals("1984 — Revised Edition", result.getTitle());
        assertEquals("Literary Fiction",       result.getGenre());
        assertEquals(author,                   result.getAuthor());
    }

    @Test
    @DisplayName("updateBook – throws when book ID not found")
    void testUpdateBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(99L, new Book(), 1L));
    }

    @Test
    @DisplayName("updateBook – throws when author ID not found")
    void testUpdateBookAuthorNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(1L, book1, 99L));
    }

    @Test
    @DisplayName("updateBook – throws on ISBN conflict with another book")
    void testUpdateBookIsbnConflict() {
        // book2 already has a different ISBN; we try to assign book1's ISBN to book2
        Book updates = new Book("Animal Farm Updated", "978-0-452-28423-4" /* book1's ISBN */, "Satire", 1945, null);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        // The ISBN belongs to book1 (id=1), not book2 (id=2)
        when(bookRepository.findByIsbn("978-0-452-28423-4")).thenReturn(Optional.of(book1));

        assertThrows(DataIntegrityViolationException.class,
                () -> bookService.updateBook(2L, updates, 1L),
                "ISBN conflict with another book should throw");
    }

    // ---- getAllBooksWithAuthorDetails ----

    @Test
    @DisplayName("getAllBooksWithAuthorDetails – delegates to INNER JOIN repository query")
    void testGetAllBooksWithAuthorDetails() {
        BookAuthorDTO dto = new BookAuthorDTO(1L, "1984", "978-0-452-28423-4",
                "Dystopian Fiction", 1949, 1L, "George Orwell", "British");
        when(bookRepository.findAllBooksWithAuthorDetails()).thenReturn(List.of(dto));

        List<BookAuthorDTO> result = bookService.getAllBooksWithAuthorDetails();

        assertEquals(1, result.size());
        assertEquals("1984",          result.get(0).getBookTitle());
        assertEquals("George Orwell", result.get(0).getAuthorName());
        assertEquals("British",       result.get(0).getNationality());
        verify(bookRepository).findAllBooksWithAuthorDetails();
    }

    // ---- getBooksByAuthor ----

    @Test
    @DisplayName("getBooksByAuthor – returns books for given author")
    void testGetBooksByAuthor() {
        when(bookRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(book1, book2));

        List<Book> result = bookService.getBooksByAuthor(1L);

        assertEquals(2, result.size());
        verify(bookRepository).findByAuthorId(1L);
    }

    // ---- deleteBook ----

    @Test
    @DisplayName("deleteBook – calls repository deleteById")
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }
}

package com.library.service;

import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthorService using Mockito.
 * No database, no Spring context — fast, isolated unit tests.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService Unit Tests")
class AuthorServiceTest {

    @Mock private AuthorRepository authorRepository;

    @InjectMocks private AuthorService authorService;

    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {
        author1 = new Author("George Orwell",   "British", "English novelist");
        author1.setId(1L);

        author2 = new Author("J.K. Rowling",    "British", "Harry Potter author");
        author2.setId(2L);
    }

    // ---- getAllAuthors ----

    @Test
    @DisplayName("getAllAuthors – returns all authors")
    void testGetAllAuthors() {
        when(authorRepository.findAll()).thenReturn(Arrays.asList(author1, author2));

        List<Author> result = authorService.getAllAuthors();

        assertEquals(2, result.size());
        verify(authorRepository, times(1)).findAll();
    }

    // ---- getAuthorById ----

    @Test
    @DisplayName("getAuthorById – returns author when found")
    void testGetAuthorByIdFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));

        Optional<Author> result = authorService.getAuthorById(1L);

        assertTrue(result.isPresent());
        assertEquals("George Orwell", result.get().getName());
    }

    @Test
    @DisplayName("getAuthorById – returns empty when not found")
    void testGetAuthorByIdNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(authorService.getAuthorById(99L).isEmpty());
    }

    // ---- saveAuthor ----

    @Test
    @DisplayName("saveAuthor – saves new author successfully")
    void testSaveAuthorSuccess() {
        Author newAuthor = new Author("Toni Morrison", "American", "Nobel winner");
        when(authorRepository.findByNameIgnoreCase("Toni Morrison")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(newAuthor);

        Author saved = authorService.saveAuthor(newAuthor);

        assertNotNull(saved);
        assertEquals("Toni Morrison", saved.getName());
        verify(authorRepository).save(newAuthor);
    }

    @Test
    @DisplayName("saveAuthor – throws exception for duplicate name")
    void testSaveAuthorDuplicateName() {
        Author duplicate = new Author("George Orwell", "British", "Duplicate");
        when(authorRepository.findByNameIgnoreCase("George Orwell"))
                .thenReturn(Optional.of(author1));

        assertThrows(DataIntegrityViolationException.class,
                () -> authorService.saveAuthor(duplicate),
                "Service should throw on duplicate author name");

        verify(authorRepository, never()).save(any());
    }

    // ---- updateAuthor ----

    @Test
    @DisplayName("updateAuthor – updates all fields correctly")
    void testUpdateAuthor() {
        Author updates = new Author("George Orwell Updated", "British-Irish", "Updated bio");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorRepository.findByNameIgnoreCase("George Orwell Updated")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

        Author result = authorService.updateAuthor(1L, updates);

        assertEquals("George Orwell Updated", result.getName());
        assertEquals("British-Irish",         result.getNationality());
        assertEquals("Updated bio",           result.getBiography());
    }

    @Test
    @DisplayName("updateAuthor – throws when author not found")
    void testUpdateAuthorNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authorService.updateAuthor(99L, new Author()));
    }

    @Test
    @DisplayName("updateAuthor – allows keeping the same name (self-update)")
    void testUpdateAuthorSameName() {
        // Same name but same ID — should not be treated as duplicate
        Author updates = new Author("George Orwell", "British-Irish", "New bio");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        // findByNameIgnoreCase returns the same author (id=1), not a different one
        when(authorRepository.findByNameIgnoreCase("George Orwell")).thenReturn(Optional.of(author1));
        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> authorService.updateAuthor(1L, updates),
                "Updating own name should not throw");
    }

    @Test
    @DisplayName("updateAuthor – throws on name conflict with DIFFERENT author")
    void testUpdateAuthorNameConflict() {
        // Try to rename author2 to author1's name
        Author updates = new Author("George Orwell", "British", "Conflict");
        when(authorRepository.findById(2L)).thenReturn(Optional.of(author2));
        // findByName returns author1 (id=1), which is different from author2 (id=2)
        when(authorRepository.findByNameIgnoreCase("George Orwell")).thenReturn(Optional.of(author1));

        assertThrows(DataIntegrityViolationException.class,
                () -> authorService.updateAuthor(2L, updates),
                "Name conflict with another author should throw");
    }

    // ---- getAuthorsWithBooks ----

    @Test
    @DisplayName("getAuthorsWithBooks – delegates to INNER JOIN query")
    void testGetAuthorsWithBooks() {
        when(authorRepository.findAuthorsWithBooks()).thenReturn(List.of(author1));

        List<Author> result = authorService.getAuthorsWithBooks();

        assertEquals(1, result.size());
        verify(authorRepository).findAuthorsWithBooks();
    }

    // ---- getAuthorBookCounts ----

 @Test
@DisplayName("getAuthorBookCounts – delegates to repository")
void testGetAuthorBookCounts() {

    List<Object[]> mockData = new ArrayList<>();
    mockData.add(new Object[]{"George Orwell", 2L});

    when(authorRepository.findAuthorBookCounts()).thenReturn(mockData);

    List<Object[]> result = authorService.getAuthorBookCounts();

    assertEquals(1, result.size());
    assertEquals("George Orwell", result.get(0)[0]);
    assertEquals(2L, result.get(0)[1]);
}

    // ---- deleteAuthor ----

    @Test
    @DisplayName("deleteAuthor – calls repository deleteById")
    void testDeleteAuthor() {
        doNothing().when(authorRepository).deleteById(1L);

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).deleteById(1L);
    }
}

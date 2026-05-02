package com.library.repository;

import com.library.entity.Book;
import com.library.entity.BookAuthorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entity.
 *
 * Key custom query: findAllBooksWithAuthorDetails() performs an INNER JOIN
 * between Book and Author and returns a list of BookAuthorDTO objects.
 * The constructor expression (new com.library.entity.BookAuthorDTO(...))
 * in JPQL lets Spring Data JPA map the result set directly to a DTO
 * without needing a native SQL result-set mapping.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ---------- Derived Queries ----------

    /** Filter books by genre (case-insensitive) */
    List<Book> findByGenreIgnoreCase(String genre);

    /** All books by a given author ID */
    List<Book> findByAuthorId(Long authorId);

    /** Duplicate ISBN check – used by service before save/update */
    Optional<Book> findByIsbn(String isbn);

    /** Keyword search on title */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /** Year range filter */
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);

    // ---------- Custom JPQL INNER JOIN Queries ----------

    /**
     * PRIMARY JOIN QUERY used by the "List Books" page.
     *
     * INNER JOIN b.author a — joins Book to its Author.
     * Because it's INNER JOIN, any book without an author is excluded
     * (our schema enforces author_id NOT NULL, so all books appear).
     *
     * The result is a list of BookAuthorDTO containing fields from both
     * tables — no lazy-loading issues in the JSP view.
     */
    @Query("SELECT new com.library.entity.BookAuthorDTO(" +
           "b.id, b.title, b.isbn, b.genre, b.publicationYear, " +
           "a.id, a.name, a.nationality) " +
           "FROM Book b INNER JOIN b.author a " +
           "ORDER BY b.title ASC")
    List<BookAuthorDTO> findAllBooksWithAuthorDetails();

    /**
     * Filtered INNER JOIN – same join but narrowed to one genre.
     * Used by BookService.getBooksByGenreWithAuthorDetails().
     */
    @Query("SELECT new com.library.entity.BookAuthorDTO(" +
           "b.id, b.title, b.isbn, b.genre, b.publicationYear, " +
           "a.id, a.name, a.nationality) " +
           "FROM Book b INNER JOIN b.author a " +
           "WHERE LOWER(b.genre) = LOWER(:genre) " +
           "ORDER BY b.title ASC")
    List<BookAuthorDTO> findBooksByGenreWithAuthorDetails(@Param("genre") String genre);
}

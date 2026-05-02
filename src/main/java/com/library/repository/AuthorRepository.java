package com.library.repository;

import com.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Author entity.
 *
 * Extends JpaRepository<Author, Long> which provides:
 *   findAll(), findById(), save(), deleteById(), count(), existsById() ...
 *
 * Spring Data JPA generates the SQL automatically from method names
 * (derived queries) and from @Query annotations (JPQL/native).
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // ---------- Derived Queries (Spring Data auto-generates SQL) ----------

    /** Case-insensitive partial name search */
    List<Author> findByNameContainingIgnoreCase(String name);

    /** Filter by exact nationality */
    List<Author> findByNationality(String nationality);

    /** Used by service to detect duplicate author names */
    Optional<Author> findByNameIgnoreCase(String name);

    // ---------- Custom JPQL Queries ----------

    /**
     * INNER JOIN – returns only authors who have at least one book.
     * Authors with zero books are excluded (that's what INNER JOIN does).
     * DISTINCT prevents duplicates when an author has multiple books.
     */
    @Query("SELECT DISTINCT a FROM Author a INNER JOIN a.books b")
    List<Author> findAuthorsWithBooks();

    /**
     * INNER JOIN with GROUP BY – returns author name and their book count,
     * ordered descending so the most prolific author comes first.
     * Returns Object[] where [0]=name (String), [1]=count (Long).
     */
    @Query("SELECT a.name, COUNT(b) " +
           "FROM Author a INNER JOIN a.books b " +
           "GROUP BY a.id, a.name " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> findAuthorBookCounts();
}

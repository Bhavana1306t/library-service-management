package com.library.entity;

/**
 * Data Transfer Object (DTO) returned by the custom INNER JOIN JPQL query.
 *
 * This is not a JPA entity — it is a plain Java class used only to carry
 * the combined result of the Book ⟵INNER JOIN⟶ Author query back to the
 * controller / JSP view.
 *
 * Spring Data JPA instantiates this class via the constructor expression
 * in the @Query annotation:
 *   SELECT new com.library.entity.BookAuthorDTO(b.id, b.title, ...) ...
 */
public class BookAuthorDTO {

    private Long    bookId;
    private String  bookTitle;
    private String  isbn;
    private String  genre;
    private Integer publicationYear;
    private Long    authorId;
    private String  authorName;
    private String  nationality;

    // All-args constructor required by JPQL constructor expression
    public BookAuthorDTO(Long bookId, String bookTitle, String isbn, String genre,
                         Integer publicationYear, Long authorId,
                         String authorName, String nationality) {
        this.bookId          = bookId;
        this.bookTitle       = bookTitle;
        this.isbn            = isbn;
        this.genre           = genre;
        this.publicationYear = publicationYear;
        this.authorId        = authorId;
        this.authorName      = authorName;
        this.nationality     = nationality;
    }

    // ---- Getters ----
    public Long    getBookId()          { return bookId; }
    public String  getBookTitle()       { return bookTitle; }
    public String  getIsbn()            { return isbn; }
    public String  getGenre()           { return genre; }
    public Integer getPublicationYear() { return publicationYear; }
    public Long    getAuthorId()        { return authorId; }
    public String  getAuthorName()      { return authorName; }
    public String  getNationality()     { return nationality; }
}

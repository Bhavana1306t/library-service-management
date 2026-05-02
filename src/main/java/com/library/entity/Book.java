package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Book entity — many side of the ManyToOne relationship with Author.
 *
 * Annotations used:
 *   @Entity       – JPA managed entity
 *   @Table        – maps to "books" table
 *   @ManyToOne    – many books belong to one author
 *   @JoinColumn   – specifies the FK column (author_id) in this table
 *   @Column(unique=true) – enforces ISBN uniqueness at DB level
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false, length = 50)
    private String genre;

    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Year must be >= 1000")
    @Max(value = 2026, message = "Year cannot be in the future")
    @Column(nullable = false)
    private Integer publicationYear;

    /**
     * @ManyToOne  – each book has exactly one author.
     * @JoinColumn – author_id FK, not-null enforced at DB level.
     * fetch LAZY  – author is loaded only when accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    // ---- Constructors ----
    public Book() {}

    public Book(String title, String isbn, String genre, Integer publicationYear, Author author) {
        this.title           = title;
        this.isbn            = isbn;
        this.genre           = genre;
        this.publicationYear = publicationYear;
        this.author          = author;
    }

    // ---- Getters & Setters ----
    public Long    getId()                         { return id; }
    public void    setId(Long id)                  { this.id = id; }

    public String  getTitle()                      { return title; }
    public void    setTitle(String title)          { this.title = title; }

    public String  getIsbn()                       { return isbn; }
    public void    setIsbn(String isbn)            { this.isbn = isbn; }

    public String  getGenre()                      { return genre; }
    public void    setGenre(String genre)          { this.genre = genre; }

    public Integer getPublicationYear()                        { return publicationYear; }
    public void    setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public Author  getAuthor()                     { return author; }
    public void    setAuthor(Author author)        { this.author = author; }
}

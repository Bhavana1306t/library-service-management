package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Author entity — one side of the OneToMany relationship with Book.
 *
 * Annotations used:
 *   @Entity          – marks this as a JPA managed entity
 *   @Table           – maps to the "authors" table
 *   @Id              – primary key
 *   @GeneratedValue  – auto-increment strategy
 *   @Column          – column constraints (nullable, length, unique)
 *   @OneToMany       – one author can have many books
 *   @NotBlank/@Size  – Bean Validation constraints
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Nationality is required")
    @Column(nullable = false, length = 100)
    private String nationality;

    @Column(length = 500)
    private String biography;

    /**
     * mappedBy = "author"  → the foreign key lives on the Book side.
     * cascade = ALL        → persist/delete books along with author.
     * fetch = LAZY         → books are only loaded when accessed (performance).
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    // ---- Constructors ----
    public Author() {}

    public Author(String name, String nationality, String biography) {
        this.name        = name;
        this.nationality = nationality;
        this.biography   = biography;
    }

    // ---- Getters & Setters ----
    public Long   getId()           { return id; }
    public void   setId(Long id)    { this.id = id; }

    public String getName()              { return name; }
    public void   setName(String name)   { this.name = name; }

    public String getNationality()                   { return nationality; }
    public void   setNationality(String nationality) { this.nationality = nationality; }

    public String getBiography()                 { return biography; }
    public void   setBiography(String biography) { this.biography = biography; }

    public List<Book> getBooks()              { return books; }
    public void       setBooks(List<Book> b)  { this.books = b; }
}

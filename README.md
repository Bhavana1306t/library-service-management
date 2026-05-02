# 📚 Library Management System — Spring Boot

A full-stack Spring Boot application managing **Books** and **Authors** with
Create, Read, and Update operations, JSP views, a custom INNER JOIN JPQL
query, Bean Validation, exception handling, and JUnit + Mockito unit tests.

---

## 🗂️ Project Structure

```
library-app/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/library/
    │   │   ├── LibraryApplication.java          # @SpringBootApplication entry point
    │   │   ├── DataInitializer.java             # Seeds 10 authors + 10 books on startup
    │   │   ├── entity/
    │   │   │   ├── Author.java                  # @Entity, @OneToMany
    │   │   │   ├── Book.java                    # @Entity, @ManyToOne, @JoinColumn
    │   │   │   └── BookAuthorDTO.java           # DTO returned by INNER JOIN query
    │   │   ├── repository/
    │   │   │   ├── AuthorRepository.java        # JpaRepository + custom JPQL queries
    │   │   │   └── BookRepository.java          # JpaRepository + INNER JOIN JPQL query
    │   │   ├── service/
    │   │   │   ├── AuthorService.java           # Business logic, duplicate detection
    │   │   │   └── BookService.java             # Business logic, ISBN uniqueness
    │   │   └── controller/
    │   │       ├── HomeController.java          # GET /
    │   │       ├── AuthorController.java        # GET/POST /authors/**
    │   │       └── BookController.java          # GET/POST /books/**
    │   ├── resources/
    │   │   └── application.properties
    │   └── webapp/WEB-INF/views/
    │       ├── style.css
    │       ├── index.jsp                        # Dashboard/home
    │       ├── books/
    │       │   ├── list.jsp                     # View all books (uses JOIN query)
    │       │   └── form.jsp                     # Add / Edit book
    │       └── authors/
    │           ├── list.jsp                     # View all authors
    │           ├── form.jsp                     # Add / Edit author
    │           └── detail.jsp                   # Single author + their books
    └── test/java/com/library/
        ├── repository/RepositoryIntegrationTest.java
        └── service/
            ├── BookServiceTest.java
            └── AuthorServiceTest.java
```

---

## ✅ Requirements Checklist

| Requirement | Status | Where |
|---|---|---|
| Two entities (Books + Authors) | ✅ | `entity/` |
| JPA annotations (`@Entity`, `@Id`, `@OneToMany`, `@ManyToOne`, `@JoinColumn`) | ✅ | `Author.java`, `Book.java` |
| 10 rows seeded in each table | ✅ | `DataInitializer.java` |
| CREATE operation (form + controller) | ✅ | `*/form.jsp`, `*Controller.java` |
| READ operation (list view) | ✅ | `books/list.jsp`, `authors/list.jsp` |
| UPDATE operation | ✅ | `*/form.jsp` (edit mode), `*Controller.java` |
| Custom INNER JOIN JPQL query | ✅ | `BookRepository.findAllBooksWithAuthorDetails()` |
| JSP pages (not Thymeleaf) | ✅ | All `.jsp` files in `WEB-INF/views/` |
| JSTL / Expression Language in JSP | ✅ | `<c:forEach>`, `${...}` throughout |
| Service layer | ✅ | `AuthorService.java`, `BookService.java` |
| Repository layer extending JpaRepository | ✅ | `AuthorRepository`, `BookRepository` |
| Exception handling (integrity violations) | ✅ | Service throws `DataIntegrityViolationException`, controller catches it |
| Bean Validation (`@NotBlank`, `@Min`, etc.) | ✅ | Entity classes + `@Valid` in controllers |
| CSS styling | ✅ | `style.css` |
| JUnit unit tests (service) | ✅ | `BookServiceTest`, `AuthorServiceTest` |
| Mockito mocks | ✅ | `@Mock`, `@InjectMocks` in service tests |
| Repository integration tests | ✅ | `RepositoryIntegrationTest` (`@DataJpaTest`) |

---

## ⚙️ How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the application
```bash
cd library-app
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8080**

### Run tests
```bash
mvn test
```

### H2 Database Console
Available at **http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: *(leave blank)*

---

## 🔑 Key Technical Decisions

### INNER JOIN Query
`BookRepository` contains a JPQL INNER JOIN that returns a `BookAuthorDTO`:
```java
@Query("SELECT new com.library.entity.BookAuthorDTO(" +
       "b.id, b.title, b.isbn, b.genre, b.publicationYear, " +
       "a.id, a.name, a.nationality) " +
       "FROM Book b INNER JOIN b.author a " +
       "ORDER BY b.title ASC")
List<BookAuthorDTO> findAllBooksWithAuthorDetails();
```
This avoids lazy-loading issues when rendering JSP views and is exactly what
the Books list page uses.

### Post-Redirect-Get (PRG) Pattern
All successful form submissions redirect to the list page.
`RedirectAttributes.addFlashAttribute()` carries the one-time success message
across the redirect so it appears in the list view.

### Validation & Exception Flow
1. `@Valid` on the controller method parameter triggers Bean Validation.
2. If field errors exist, `BindingResult.hasErrors()` is true → form is
   re-rendered with inline error messages.
3. Business rule violations (duplicate ISBN/name) are caught from the
   service layer as `DataIntegrityViolationException` and shown as a
   top-level `alert-danger` banner.

---

## 📋 URL Reference

| URL | Method | Description |
|---|---|---|
| `/` | GET | Dashboard |
| `/books` | GET | List all books (INNER JOIN) |
| `/books/new` | GET | Add book form |
| `/books/save` | POST | Create book |
| `/books/edit/{id}` | GET | Edit book form |
| `/books/update/{id}` | POST | Update book |
| `/authors` | GET | List all authors |
| `/authors/new` | GET | Add author form |
| `/authors/save` | POST | Create author |
| `/authors/edit/{id}` | GET | Edit author form |
| `/authors/update/{id}` | POST | Update author |
| `/authors/{id}` | GET | Author detail + their books |
| `/h2-console` | GET | H2 database console |

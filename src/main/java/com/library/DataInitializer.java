package com.library;

import com.library.entity.Author;
import com.library.entity.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with 10 authors and 10 books on application startup.
 *
 * Uses a guard (count() == 0) so data is only inserted once even if the
 * application is restarted without recreating the schema.
 *
 * Entities are saved via the repositories directly to avoid triggering
 * the duplicate-name check in AuthorService (which is a feature, not a bug,
 * but would fail on re-seed).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private AuthorRepository authorRepository;
    @Autowired private BookRepository   bookRepository;

    @Override
    public void run(String... args) {
        // Guard: only seed when tables are empty
        if (authorRepository.count() > 0) {
            System.out.println("ℹ️  Database already populated — skipping seed.");
            return;
        }

        // ============================================================
        //  10 Authors
        // ============================================================
        Author a1  = authorRepository.save(new Author("George Orwell",
                "British",
                "Eric Arthur Blair, known by pen name George Orwell, was an English novelist, " +
                "essayist, journalist and critic. His work is characterised by lucid prose, " +
                "awareness of social injustice, opposition to totalitarianism."));

        Author a2  = authorRepository.save(new Author("J.K. Rowling",
                "British",
                "Best known for the Harry Potter series, which has sold over 500 million copies " +
                "worldwide and been translated into 80 languages."));

        Author a3  = authorRepository.save(new Author("Haruki Murakami",
                "Japanese",
                "Contemporary Japanese author known for surrealist fiction blending Japanese " +
                "culture with Western influences. His works explore loneliness and identity."));

        Author a4  = authorRepository.save(new Author("Gabriel García Márquez",
                "Colombian",
                "Nobel Prize-winning author and pioneer of magical realism in Latin American " +
                "literature. Best known for One Hundred Years of Solitude."));

        Author a5  = authorRepository.save(new Author("Toni Morrison",
                "American",
                "Nobel Prize-winning author known for exploring the African-American experience " +
                "through powerful narratives. Her novel Beloved won the Pulitzer Prize."));

        Author a6  = authorRepository.save(new Author("Fyodor Dostoevsky",
                "Russian",
                "19th-century Russian novelist renowned for psychological depth and exploration " +
                "of the human condition. Works include Crime and Punishment and The Brothers Karamazov."));

        Author a7  = authorRepository.save(new Author("Chimamanda Ngozi Adichie",
                "Nigerian",
                "Award-winning author whose works explore race, gender, and identity in both " +
                "Nigeria and the West. Her TED talk 'We Should All Be Feminists' reached millions."));

        Author a8  = authorRepository.save(new Author("Kazuo Ishiguro",
                "British-Japanese",
                "Nobel Prize-winning author known for restrained prose and themes of memory " +
                "and regret. The Remains of the Day won the Booker Prize."));

        Author a9  = authorRepository.save(new Author("Isabel Allende",
                "Chilean",
                "Renowned Latin American author known for blending history and magical realism " +
                "in sweeping family sagas. The House of the Spirits is her most celebrated work."));

        Author a10 = authorRepository.save(new Author("Ursula K. Le Guin",
                "American",
                "Influential science fiction and fantasy author celebrated for imaginative " +
                "world-building and social commentary. A central figure of 20th-century speculative fiction."));

        // ============================================================
        //  10 Books  (one per author)
        // ============================================================
        bookRepository.save(new Book("1984",
                "978-0-452-28423-4", "Dystopian Fiction", 1949, a1));

        bookRepository.save(new Book("Harry Potter and the Philosopher's Stone",
                "978-0-7475-3269-9", "Fantasy", 1997, a2));

        bookRepository.save(new Book("Norwegian Wood",
                "978-0-375-70402-7", "Literary Fiction", 1987, a3));

        bookRepository.save(new Book("One Hundred Years of Solitude",
                "978-0-06-088328-7", "Magical Realism", 1967, a4));

        bookRepository.save(new Book("Beloved",
                "978-1-4000-3341-6", "Historical Fiction", 1987, a5));

        bookRepository.save(new Book("Crime and Punishment",
                "978-0-14-044913-6", "Psychological Fiction", 1866, a6));

        bookRepository.save(new Book("Purple Hibiscus",
                "978-1-61695-677-6", "Literary Fiction", 2003, a7));

        bookRepository.save(new Book("The Remains of the Day",
                "978-0-679-73172-5", "Literary Fiction", 1989, a8));

        bookRepository.save(new Book("The House of the Spirits",
                "978-0-553-38380-1", "Magical Realism", 1982, a9));

        bookRepository.save(new Book("The Left Hand of Darkness",
                "978-0-441-47812-5", "Science Fiction", 1969, a10));

        System.out.println("✅ Sample data loaded: 10 Authors and 10 Books");
    }
}

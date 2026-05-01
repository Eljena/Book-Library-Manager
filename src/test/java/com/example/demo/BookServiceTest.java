package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookServiceTest {

    private BookRepository bookRepository;

    private BookService bookService;

    public BookServiceTest(){
        List<Book> BOOKS = List.of(
                new Book("Fehlstart", "Marion Messina", 168, 2020, true,
                        "fehlstart.jpg", "Drama"),
                new Book("Der gute Mensch von Sezuan", "Bertolt Brecht", 160, 1943,
                        false, "sezuan.jpg", "Drama"),
                new Book("1984", "George Orwell", 384, 1949, false,
                        "1984.jpg", "Drama"),
                new Book("Die Verwandlung", "Franz Kafka", 70, 1915, false,
                "verwandlung.jpg", "Drama"));

        BookRepository bookRepositoryMock = mock(BookRepository.class);
        System.out.println(bookRepositoryMock.getClass());
        when(bookRepositoryMock.findAll()).thenReturn(BOOKS);
        bookService = new BookService(bookRepositoryMock);
    }

    /**
     * Hier wird Repo gemockt und dann in den Service manuell injiziert
     */
    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    public void testFilterBooksByTitleOrAuthor_Brecht(){
        //Abrufen aller Bücher aus dem Mock-Repository
        List<Book> allBooks = (List<Book>) bookService.getAllBooks();

        //Suche nach Büchern, die von "Bertolt Brecht" geschrieben sind
        String searchQuery = "Bertolt Brecht";
        List<Book> brechtBooks = bookService.filterBooksByTitleOrAuthor(allBooks, searchQuery);

        //Überprüfen, dass das gefundene Buch den richtigen Autor hat
        assertEquals(List.of("Bertolt Brecht"), brechtBooks.stream()
                .map(Book::getAuthor)
                .collect(Collectors.toList()));
        assertEquals(1, brechtBooks.size());
    }
   


    @Test
    public void testFilterBooksByReadStatus_Read(){
        //Abrufen aller Bücher aus dem Mock-Repository
        List<Book> allBooks = (List<Book>) bookService.getAllBooks();

        //Filtere Bücher mit dem Status true (gelesen)
        List<Book> readBooks = bookService.filterBooksByReadStatus(allBooks, true);

        //Überprüfen, dass nur die gelesenen Bücher zurückgegeben werden
        assertEquals(List.of("Fehlstart"), readBooks.stream()
                .map(Book::getTitle)
                .collect(Collectors.toList()));
        assertEquals(1, readBooks.size());
    }

    @Test
    public void testFilterBooksByReadStatus_NotRead() {
        // Abrufen aller Bücher aus dem Mock-Repository
        List<Book> allBooks = (List<Book>) bookService.getAllBooks();

        //Filtere Bücher mit dem Status false (nur nicht gelesene Bücher)
        List<Book> notReadBooks = bookService.filterBooksByReadStatus(allBooks, false);

        //Überprüfen, dass nur die nicht gelesenen Bücher zurückgegeben werden
        assertEquals(List.of("Der gute Mensch von Sezuan", "1984", "Die Verwandlung"), notReadBooks.stream()
                .map(Book::getTitle)
                .collect(Collectors.toList()));
        assertEquals(3, notReadBooks.size());
    }

    @Test
    public void testFilterBooksByReadStatus_All() {
        //Abrufen aller Bücher aus dem Mock-Repository
        List<Book> allBooks = (List<Book>) bookService.getAllBooks();

        //Wenn bookStatus null ist, sollten alle Bücher zurückgegeben werden
        List<Book> allBooksResult = bookService.filterBooksByReadStatus(allBooks, null);

        //Überprüfen, dass alle Bücher zurückgegeben werden
        assertEquals(List.of("Fehlstart", "Der gute Mensch von Sezuan", "1984", "Die Verwandlung"), allBooksResult.stream()
                .map(Book::getTitle)
                .collect(Collectors.toList()));
        assertEquals(4, allBooksResult.size());
    }

    @Test
    void testBooksByGenre_Fantasy() {
        //arrange
        Book fantasyBook = new Book("Herr der Ringe", "Tolkien", 200, 1902, false, "", "Fantasy");
        List<Book> expectedBooks = List.of(fantasyBook);

        // wenn die Methode aufgerufen wird, dann soll das zurückgegeben werden
        when(bookRepository.findByGenre("Fantasy")).thenReturn(expectedBooks);

        //act
        List<Book> result = bookService.findBooksByGenre("Fantasy");

        //assert
        assertThat(expectedBooks).isEqualTo(result);
    }



}



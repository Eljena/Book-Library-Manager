package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Nimmt HTTP-Anfragen entgegen und kommuniziert mit dem BookService, um den Client
 * die entsprechende Antwort zu liefern
 */
@Controller
@Transactional //stellt sicher, dass wenn alles in der Methode richtig ausgeführt wird, Daten persistent gespeichert werden, oder bei einem Fehler zurückgesetzt
public class BookController {

    @Autowired
    private BookService bookService;

    //Startseite
    @GetMapping("/")
    public String showIndexPage(){
        //View index zurückgeben, um Startseite anzuzeigen
        return "index";
    }

    //Seite zum Hinzufügen eines Buches
    @GetMapping("/addBook")
    public String showAddBook(){
        //View addBook zurückgeben, um "Buch hinzufügen"-Seite anzuzeigen
        return "addBook";
    }

    /**
     * Seite zum Anzeigen der Buchdetails
     */
    @GetMapping("/bookDetails")
    public String showBookDetails(@RequestParam("id") Long id, Model model){
        //Buch anhand der ID aus der DB abrufen
        Book book = bookService.getBookById(id);

        //Buchdaten ins Model packen, um Details des Buches anzuzeigen
        model.addAttribute("book", book);

        //View bookDetails zurückgeben, um die Buchdetails anzuzeigen
        return "bookDetails";
    }

    //Seite zum Bearbeiten des Buches
    @GetMapping("/updateBook")
    public String showUpdateBook(@RequestParam("id") Long id, Model model){
        // Buch anhand der ID aus der Datenbank abrufen
        Book book = bookService.getBookById(id);

        //Buchdaten ins Model packen, um im Formular angezeigt zu werden
        model.addAttribute("book", book);

        //View "updateBook" zurückgeben, um das Bearbeitungsformular anzuzeigen
        return "updateBook";
    }

    /**
     * Zeigt die Büchersammlung-Seite an, filtert und sortiert ggf.
     */
    @GetMapping("/library")
    public String listBooks(Model model,
                            @RequestParam(required = false, defaultValue = "") String sortBy,
                            @RequestParam(required = false, defaultValue = "") Boolean bookStatus,
                            @RequestParam(required = false, defaultValue = "") String searchQuery) {
        //Anzahl der Bücher ermitteln
        long bookCount = bookService.numberOfBooks();

        //Gefilterte und sortierte Bücher aus dem Service abrufen
        List<Book> books = bookService.getFilteredAndSortedBooks(bookStatus, sortBy, searchQuery);

        //Daten in das Model packen, um in der Buch-Übersichtseite angezeigt zu werden
        model.addAttribute("books", books);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("bookStatus", bookStatus);
        model.addAttribute("bookCount", bookCount);
        return "library";
    }

    /**
     * Verarbeitet das Hinzufügen eines neuen Buches
     */
    @PostMapping("/library")
    public String createBook(@ModelAttribute Book book,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        //Bild-Upload behandeln und Dateinamen speichern
        String fileName = bookService.handleImageUpload(imageFile, null);
        book.setBookcover(fileName);

        //Buch speichern
        bookService.add(book);
        return "redirect:/library";
    }

    /**
     * Verarbeitet das Ändern eines Buches
     */
    @PostMapping("/updateBook/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam("existingBookcover") String existingBookcover) throws IOException{
        //ID des Buches setzen
        book.setId(id);

        //Bild-Upload behandeln und Dateinamen speichern
        String fileName = bookService.handleImageUpload(imageFile, existingBookcover);
        book.setBookcover(fileName);

        //Buch aktualisieren
        bookService.update(id, book);
        return "redirect:/library";
    }

    /**
     * Verarbeitet das Löschen eines Buches
     */
    @PostMapping("delete/{id}")
    public String deleteBook(@PathVariable("id") Long id){
        bookService.delete(id);
        return "redirect:/library";
    }

}

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
@Transactional
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String showIndexPage(){
        return "index";
    }

    @GetMapping("/addBook")
    public String showAddBook(){
        return "addBook";
    }

    /**
     * Seite zum Anzeigen der Buchdetails
     */
    @GetMapping("/bookDetails")
    public String showBookDetails(@RequestParam("id") Long id, Model model){
        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);

        return "bookDetails";
    }

    @GetMapping("/updateBook")
    public String showUpdateBook(@RequestParam("id") Long id, Model model){
        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);

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
        long bookCount = bookService.numberOfBooks();

        List<Book> books = bookService.getFilteredAndSortedBooks(bookStatus, sortBy, searchQuery);

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
        String fileName = bookService.handleImageUpload(imageFile, null);
        book.setBookcover(fileName);

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
        book.setId(id);

        String fileName = bookService.handleImageUpload(imageFile, existingBookcover);
        book.setBookcover(fileName);

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

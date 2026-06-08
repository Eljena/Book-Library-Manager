package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * enthält die Geschäftslogik und verwaltet die Interaktionen mit dem Repository
 */

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    /**
     * Liefert alle Buecher zurueck
     * @return
     */
    public Iterable<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    /**
     * Liefert das Buch mit der übergebenden ID zurück
     * @param id
     * @return
     */
    public Book getBookById(long id){
        return bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Fügt ein neues Buch zur DB hinzu
     * @param newBook
     */
    public void add(Book newBook){
        bookRepository.save(newBook);
    }

    /**
     * Wählt das Buch mit der übergebenen ID aus und überschreibt die Eigenschaften
     * mit den neuen Eigenschaften des Buches
     * @param id
     * @param updatedBook
     */
    public void update(Long id, Book updatedBook) {
        bookRepository.findById(id).ifPresent(existingBook -> {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setPublishYear(updatedBook.getPublishYear());
            existingBook.setPages(updatedBook.getPages());
            existingBook.setBookcover(updatedBook.getBookcover());
            existingBook.setRead(updatedBook.isRead());

            bookRepository.save(existingBook);
        });
    }

    /**
     * Zur Verarbeitung des Bild-Uploads
     */
    public String handleImageUpload(MultipartFile imageFile, String existingBookcover) throws IOException {
        if (!imageFile.isEmpty()) {
            String uploadDir = "uploads/";

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = imageFile.getOriginalFilename();

            Path filePath = Paths.get(uploadDir + fileName);

            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }

        if(existingBookcover != null && !existingBookcover.isEmpty()){
            return existingBookcover;
        }
        return "no_cover.png";
    }

    /**
     * Löscht das Buch anhand der übergebenen ID
     * @param id
     */
    public void delete(Long id){
        bookRepository.deleteById(id);
    }

    /**
     * Liefert die Anzahl der gespeicherten Bücher in der DB
     * @return
     */
    public long numberOfBooks(){
        return bookRepository.count();
    }

    /**
     * Filtert und sortiert Bücher nach Autor und Sortierkriterium
     *
     * @param bookStatus
     * @param sortBy
     * @param searchQuery
     * @return
     */
    public List<Book> getFilteredAndSortedBooks(Boolean bookStatus, String sortBy, String searchQuery) {
        List<Book> books = new ArrayList<>(StreamSupport.stream(bookRepository.findAll().spliterator(), false).toList());

        books = new ArrayList<>(filterBooksByTitleOrAuthor(books, searchQuery));

        books = new ArrayList<>(filterBooksByReadStatus(books, bookStatus));

        sortBooks(books, sortBy);

        return books;
    }

    /**
     * Filtert nach Autor*innen oder Titel
     */
    public List<Book> filterBooksByTitleOrAuthor(List<Book> books, String searchQuery){
        List<Book> filteredBooks = new ArrayList<>();

        searchQuery = searchQuery.trim();   //entfernt Leerzeichen am Anfang und Ende eines Strings
        if(!searchQuery.isEmpty()){
            String finalSearchQuery = searchQuery;
            filteredBooks = books
                    .stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(finalSearchQuery.toLowerCase()) ||
                            book.getAuthor().toLowerCase().contains(finalSearchQuery.toLowerCase())).toList();
        } else {
            filteredBooks = books;
        }

        return filteredBooks;
    }

    /**
     * Filtert nach Buchstatus und liefert die passenden Bücher zurück
     * @param bookStatus
     * @return
     */
    public List<Book> filterBooksByReadStatus(List<Book> books, Boolean bookStatus){
        return  books
                .stream()
                .filter(book -> bookStatus == null || bookStatus == book.isRead())
                .toList();

    }

    /**
     * Sortiert die Bücher nach einem Kriterium
     * @param sortBy
     * @return
     */
    public void sortBooks(List<Book> books, String sortBy){
        //Sortiert die Bücher je nach Auswahl
        if (!sortBy.isEmpty()) {
            switch (sortBy) {
                case "read" -> books.sort(Comparator.comparing(Book::isRead).reversed());
                case "notRead" -> books.sort(Comparator.comparing(Book::isRead));
                case "title" -> books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
                case "author" -> books.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
                case "pages" -> books.sort(Comparator.comparing(Book::getPages));
            }
        }

    }


    public List<Book> findBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }
}


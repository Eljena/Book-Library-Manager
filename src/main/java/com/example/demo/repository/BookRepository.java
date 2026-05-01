package com.example.demo.repository;

import com.example.demo.model.Book;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Verantwortlich für den Datenbankzugriff (CRUD-Operationen)
 *
 * Statt von CrudRepository von JpaRepository erben -> dabei liefert findAll() eine Liste zurück
 */

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByGenre(String genre);
}

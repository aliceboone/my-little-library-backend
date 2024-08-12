package com.capstone.mylittlelibrarybackend.bookTest;

import com.capstone.mylittlelibrarybackend.book.Book;
import com.capstone.mylittlelibrarybackend.book.BookRepository;
import com.capstone.mylittlelibrarybackend.book.BookService;
import com.capstone.mylittlelibrarybackend.book.BookNotFoundException;
import com.capstone.mylittlelibrarybackend.user.User;
import com.capstone.mylittlelibrarybackend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookTestInt {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBooks() {
        // Arrange
        User user = new User();
        user.setId(1L);
        List<Book> books = new ArrayList<>();
        books.add(new Book("Title1", "Author1", "Genre1", "2020", "Description1", "English", null, user));
        books.add(new Book("Title2", "Author2", "Genre2", "2021", "Description2", "English", null, user));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findByUserId(1L)).thenReturn(books);

        // Act
        List<Book> result = bookService.getBooks(1L);

        // Assert
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testUpdateBook_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Book book = new Book("Title1", "Author1", "Genre1", "2020", "Description1", "English", null, user);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        bookService.updateBook(1L, book, 1L);

        // Assert
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testUpdateBook_NotFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Book book = new Book("Title1", "Author1", "Genre1", "2020", "Description1", "English", null, user);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, book, 1L));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testDeleteBook_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Book book = new Book("Title1", "Author1", "Genre1", "2020", "Description1", "English", null, user);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        // Act
        bookService.deleteBook(1L, 1L);

        // Assert
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void testDeleteBook_NotFound() {
        // Arrange
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L, 1L));
        verify(bookRepository, never()).delete(any(Book.class));
    }
}

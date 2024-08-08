package com.capstone.mylittlelibrarybackend.book;

import com.capstone.mylittlelibrarybackend.user.User;
import com.capstone.mylittlelibrarybackend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getBooks(Long userId) {
        // Ensure the user exists
        userRepository.findById(userId).orElseThrow();
        return bookRepository.findByUserId(userId);
    }

    public Book getBookById(Long bookId, Long userId) {
        return bookRepository.findById(bookId)
                .filter(book -> book.getUser().getId().equals(userId))
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    public void addNewBook(Book book, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        book.setUser(user);
        bookRepository.save(book);
    }

    public void updateBook(Long bookId, Book updatedBook, Long userId) {
        Book existingBook = getBookById(bookId, userId);
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setPublishedYear(updatedBook.getPublishedYear());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setLanguage(updatedBook.getLanguage());
        existingBook.setImage(updatedBook.getImage());
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long bookId, Long userId) {
        Book book = getBookById(bookId, userId);
        bookRepository.delete(book);
    }

    public List<Book> searchBooks(String title, String author, String genre, String language, Long userId) {
        return bookRepository.searchBooksByUser(title, author, genre, language, userId);
    }
}

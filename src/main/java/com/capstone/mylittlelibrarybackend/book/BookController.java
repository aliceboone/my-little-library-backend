package com.capstone.mylittlelibrarybackend.book;

import com.capstone.mylittlelibrarybackend.imageupload.UploadImage;
import com.capstone.mylittlelibrarybackend.user.User;
import com.capstone.mylittlelibrarybackend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/books")
public class BookController {

    private final BookService bookService;
    private final UploadImage uploadImage;
    private final UserRepository userRepository;

    @Autowired
    public BookController(BookService bookService, UploadImage uploadImage, UserRepository userRepository) {
        this.bookService = bookService;
        this.uploadImage = uploadImage;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            String email = (String) oauth2User.getAttributes().get("email");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
            return user.getId();
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Book> getBooks() {
        Long userId = getCurrentUserId();
        return bookService.getBooks(userId);
    }

    @GetMapping(path = "/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public Book getBookById(@PathVariable("bookId") Long bookId) {
        Long userId = getCurrentUserId();
        return bookService.getBookById(bookId, userId);
    }

    @GetMapping(path = "/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "author", defaultValue = "") String author,
            @RequestParam(value = "genre", defaultValue = "") String genre,
            @RequestParam(value = "language", defaultValue = "") String language
    ) {
        Long userId = getCurrentUserId();
        List<Book> books = bookService.searchBooks(title, author, genre, language, userId);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addNewBook(@RequestParam("title") String title,
                                             @RequestParam("author") String author,
                                             @RequestParam("genre") String genre,
                                             @RequestParam("publishedYear") String publishedYear,
                                             @RequestParam("description") String description,
                                             @RequestParam("language") String language,
                                             @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = uploadImage.uploadImage(image);
            }

            Book book = new Book(title, author, genre, publishedYear, description, language, imagePath);
            Long userId = getCurrentUserId();
            bookService.addNewBook(book, userId);

            return ResponseEntity.ok("Book added successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add book: " + e.getMessage());
        }
    }

    @PutMapping(path = "/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateBook(@PathVariable("bookId") Long bookId,
                                             @RequestParam("title") String title,
                                             @RequestParam("author") String author,
                                             @RequestParam("genre") String genre,
                                             @RequestParam("publishedYear") String publishedYear,
                                             @RequestParam("description") String description,
                                             @RequestParam("language") String language,
                                             @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Long userId = getCurrentUserId();
            Book existingBook = bookService.getBookById(bookId, userId);
            if (image != null && !image.isEmpty()) {
                String imagePath = uploadImage.uploadImage(image);
                existingBook.setImage(imagePath);
            }

            existingBook.setTitle(title);
            existingBook.setAuthor(author);
            existingBook.setGenre(genre);
            existingBook.setPublishedYear(publishedYear);
            existingBook.setDescription(description);
            existingBook.setLanguage(language);

            bookService.updateBook(bookId, existingBook, userId);

            return ResponseEntity.ok("Book successfully updated");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update book: " + e.getMessage());
        }
    }

    @DeleteMapping(path = "/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteBook(@PathVariable("bookId") Long bookId) {
        try {
            Long userId = getCurrentUserId();
            bookService.deleteBook(bookId, userId);
            return ResponseEntity.ok("Book successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete book: " + e.getMessage());
        }
    }
}

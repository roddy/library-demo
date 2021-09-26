package app.roddy.librarydemo.rest;

import app.roddy.librarydemo.DataService;
import app.roddy.librarydemo.LibraryBookService;
import app.roddy.librarydemo.database.DbBook;
import app.roddy.librarydemo.models.BookEvent;
import app.roddy.librarydemo.models.BookEventType;
import app.roddy.librarydemo.rest.models.BookDetails;
import app.roddy.librarydemo.rest.models.BookListing;
import app.roddy.librarydemo.rest.models.BookListingItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final DataService dataService;
    private final LibraryBookService libraryBookService;

    public BookController(DataService dataService, LibraryBookService libraryBookService) {
        this.dataService = dataService;
        this.libraryBookService = libraryBookService;
    }

    @GetMapping
    public ResponseEntity<BookListing> listBooks() {
        List<DbBook> allBooks = this.dataService.getAllBooks();

        int checkedOut = 0;
        int available = 0;
        List<BookListingItem> books = new ArrayList<>();
        for (DbBook book : allBooks) {
            boolean isCheckedOut = book.getBorrowedBy() != null;

            books.add(new BookListingItem(book.getId(), book.getTitle(), book.getAuthor(), isCheckedOut));
            if (isCheckedOut) {
                checkedOut++;
            } else {
                available++;
            }
        }
        BookListing listing = new BookListing(checkedOut, available, checkedOut + available, books);
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetails> getBookDetails(@PathVariable Integer id) {
        DbBook book = this.dataService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        BookDetails output = new BookDetails(book.getTitle(), book.getAuthor(), book.getDescription(), book.getYear(), book.getBorrowedBy() != null);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> invokeAction(@PathVariable(name = "id") Integer bookId,
                                             @RequestParam BookEventType action,
                                             @RequestParam(required = false, name = "patronId") Integer userId) {

        BookEvent event = new BookEvent();
        event.setBookId(bookId);
        event.setUserId(userId);
        event.setType(action);

        boolean success = this.libraryBookService.processBookEvent(event);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}


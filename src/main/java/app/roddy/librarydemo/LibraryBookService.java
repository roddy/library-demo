package app.roddy.librarydemo;

import app.roddy.librarydemo.database.DbBook;
import app.roddy.librarydemo.database.DbUser;
import app.roddy.librarydemo.models.BookEvent;
import app.roddy.librarydemo.models.BookEventResult;
import app.roddy.librarydemo.models.BookEventType;
import app.roddy.librarydemo.models.CheckedOutBook;
import app.roddy.librarydemo.models.Person;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Service
public class LibraryBookService {

    private final DataService dataService;
    private final KieContainer kContainer;
    private final Logger logger = LoggerFactory.getLogger(LibraryBookService.class);

    public LibraryBookService(DataService dataService, KieContainer kContainer) {
        this.dataService = dataService;
        this.kContainer = kContainer;
    }

    public BookEventResult processBookEvent(BookEvent event) {
        // First, get the necessary data from the database
        Person person = getUserFromEvent(event);

        // Use this object to track the rule results
        BookEventResult result = new BookEventResult();

        KieSession session = this.getSession();
        session.insert(person);
        session.insert(event);
        session.setGlobal("result", result);
        session.fireAllRules();

        // If the action is not allowed, log some info and return false
        // If the action is allowed, perform the action as requested
        if (!result.isSuccess()) {
            logger.info("Failed to checkout book. Reason = "+result.getReason());
        } else if (event.getType() == BookEventType.BORROW){
            borrowBook(event);
        } else {
            returnBook(event.getBookId());
        }
        return result;
    }

    private void borrowBook(BookEvent event) {
        OffsetDateTime borrowDate = OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0);
        DbUser user = dataService.getUserById(event.getUserId());
        DbBook book = this.dataService.getBookById(event.getBookId());

        book.setBorrowedBy(user);
        book.setBorrowedOn(borrowDate);
        this.dataService.saveBook(book);
    }

    private void returnBook(Integer bookId) {
        DbBook book = this.dataService.getBookById(bookId);
        book.setBorrowedBy(null);
        book.setBorrowedOn(null);
        this.dataService.saveBook(book);
    }

    private Person getUserFromEvent(BookEvent event) {
        DbUser userFromDb = dataService.getUserById( event.getUserId() );

        if (userFromDb == null) {
            return null;
        }

        Person person = new Person();
        person.setId(userFromDb.getId());
        person.setCheckedOutBooks(
                userFromDb.getCheckedOutBooks()
                        .stream()
                        .map( b -> {
                            CheckedOutBook book = new CheckedOutBook();
                            book.setId(b.getId());
                            book.setCheckedOutOn(b.getBorrowedOn());
                            return book;
                        })
                        .collect(Collectors.toList())
        );
        return person;
    }

    private KieSession getSession() {
        KieBase kBase = this.kContainer.getKieBase("books");
        return kBase.newKieSession();
    }
}

package rules;

import app.roddy.librarydemo.models.BookEvent;
import app.roddy.librarydemo.models.BookEventResult;
import app.roddy.librarydemo.models.BookEventType;
import app.roddy.librarydemo.models.CheckedOutBook;
import app.roddy.librarydemo.models.Person;
import org.droolsassert.DroolsAssert;
import org.droolsassert.DroolsSession;
import org.droolsassert.TestRules;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DroolsSession(resources = {"classpath*:/rules/*.drl"},
               builderProperties = "drools.dump.dir = target/dump",
               logResources = true)
public class RulesTest {

    @RegisterExtension
    public DroolsAssert drools = new DroolsAssert();

    @AfterEach
    void cleanup() {
        drools.setGlobal("result", null);
    }

    @Test
    @TestRules(expected = "Person has 3+ existing checked out books")
    void testThreeCheckOutBooks() {
        BookEventResult result = new BookEventResult();
        drools.setGlobal("result", result);

        BookEvent event = createBookEvent(BookEventType.BORROW, 3, 16);
        Person person = createPersonWithBooks(3, 3);

        drools.insert(person);
        drools.insertAndFire(event);

        assertFalse(result.isSuccess());
        assertEquals("User has already checked out 3 books", result.getReason());
    }

    @Test
    @TestRules(expected = "Person has an overdue book")
    void testOverdueBook() {
        BookEventResult result = new BookEventResult();
        drools.setGlobal("result", result);

        BookEvent event = createBookEvent(BookEventType.BORROW, 7, 12);
        Person person = createPersonWithBooks(7, 1);

        OffsetDateTime checkOutDate = OffsetDateTime.now().minusMonths(6);
        person.getCheckedOutBooks().get(0).setCheckedOutOn(checkOutDate);

        drools.insert(person);
        drools.insertAndFire(event);

        assertFalse(result.isSuccess());
        assertEquals("User has overdue book(s)", result.getReason());
    }

    @Test
    @TestRules(expected = "Book is returned")
    void testBookReturn() {
        BookEventResult result = new BookEventResult();
        drools.setGlobal("result", result);

        BookEvent event = createBookEvent(BookEventType.RETURN, null, 66);

        drools.insertAndFire(event);

        assertTrue(result.isSuccess());
        assertNull(result.getReason());
    }

    private BookEvent createBookEvent(BookEventType type, Integer userId, Integer bookId) {
        BookEvent event = new BookEvent();
        event.setType(type);
        event.setUserId(userId);
        event.setBookId(bookId);
        return event;
    }

    private Person createPersonWithBooks(Integer userId, int bookCount) {
        Person person = new Person();
        person.setId(userId);

        List<CheckedOutBook> books = new ArrayList<>();
        for( int i = 0; i < bookCount; i++) {
            CheckedOutBook book = new CheckedOutBook();
            book.setId(i+1);
            book.setCheckedOutOn(OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0));
            books.add(book);
        }
        person.setCheckedOutBooks(books);
        return person;
    }
}

package app.roddy.librarydemo.rest;

import app.roddy.librarydemo.DataService;
import app.roddy.librarydemo.LibraryBookService;
import app.roddy.librarydemo.database.DbBook;
import app.roddy.librarydemo.database.DbUser;
import app.roddy.librarydemo.models.BookEvent;
import app.roddy.librarydemo.models.BookEventResult;
import app.roddy.librarydemo.models.BookEventType;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DataService dataService;

    @MockBean
    LibraryBookService libraryBookService;

    @AfterEach
    void resetMocks() {
        Mockito.verifyNoMoreInteractions(this.dataService, this.libraryBookService);
        Mockito.reset(this.dataService, this.libraryBookService);
    }

    @Test
    void testEmptyList() throws Exception{
        this.mvc.perform(get("/books"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.checkedOut", is(0)))
                .andExpect(jsonPath("$.available", is(0)))
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.books", is(empty())));

        Mockito.verify(this.dataService).getAllBooks();
    }

    @Test
    void testListWithBooks() throws Exception {
        DbBook availableBk = new DbBook(1, "Available Book", "Adam", "This book can be checked out", 2021, null, null);
        DbBook checkedOutBk = new DbBook(2, "Checked Out Book", "Bob", "This book has already been checked out", 2021, new DbUser(), OffsetDateTime.now());
        List<DbBook> expectedBooks = Arrays.asList(availableBk, checkedOutBk);

        Mockito.when(dataService.getAllBooks()).thenReturn(expectedBooks);

        this.mvc.perform(get("/books"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.checkedOut", is(1)))
                .andExpect(jsonPath("$.available", is(1)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.books", IsCollectionWithSize.hasSize(2)))

                .andExpect(jsonPath("$.books[0].id", is(availableBk.getId())))
                .andExpect(jsonPath("$.books[0].title", is(availableBk.getTitle())))
                .andExpect(jsonPath("$.books[0].author", is(availableBk.getAuthor())))
                .andExpect(jsonPath("$.books[0].checkedOut", is(false)))

                .andExpect(jsonPath("$.books[1].id", is(checkedOutBk.getId())))
                .andExpect(jsonPath("$.books[1].title", is(checkedOutBk.getTitle())))
                .andExpect(jsonPath("$.books[1].author", is(checkedOutBk.getAuthor())))
                .andExpect(jsonPath("$.books[1].checkedOut", is(true)));

        Mockito.verify(this.dataService).getAllBooks();
    }

    @Test
    void testGetBookDetails() throws Exception {
        DbBook book = new DbBook(66, "Test Book", "Anne Author", "This is a test book", 2021, new DbUser(), OffsetDateTime.now());
        Mockito.when(dataService.getBookById(Mockito.anyInt())).thenReturn(book);

        this.mvc.perform(get("/books/66"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())))
                .andExpect(jsonPath("$.description", is(book.getDescription())))
                .andExpect(jsonPath("$.year", is(book.getYear())))
                .andExpect(jsonPath("$.checkedOut", is(true)));

        Mockito.verify(dataService).getBookById(66);
    }

    @Test
    void testGetBookDetailsNotFound() throws Exception {
        this.mvc.perform(get("/books/66"))
                .andExpect(status().isNotFound());

        Mockito.verify(dataService).getBookById(66);
    }

    @Test
    void testInvokeActionSuccess() throws Exception {
        BookEventResult expected = new BookEventResult();
        Mockito.when(this.libraryBookService.processBookEvent(Mockito.any(BookEvent.class))).thenReturn(expected);

        this.mvc.perform(post("/books/66?action=RETURN"))
                .andDo(print())
                .andExpect(status().isNoContent());

        ArgumentCaptor<BookEvent> captor = ArgumentCaptor.forClass(BookEvent.class);
        Mockito.verify(this.libraryBookService).processBookEvent(captor.capture());

        BookEvent arg = captor.getValue();
        assertNotNull(arg);
        assertAll(
                () -> assertEquals(66, arg.getBookId(), "book id should match url path variable"),
                () -> assertEquals(BookEventType.RETURN, arg.getType(), "type should be RETURN"),
                () -> assertNull(arg.getUserId(), "no user id was specified as a query param")
        );
    }

    @Test
    void testInvokeActionFailure() throws Exception {
        BookEventResult expected = new BookEventResult();
        expected.setSuccess(false);
        expected.setReason("This is testing a failure");

        Mockito.when(this.libraryBookService.processBookEvent(Mockito.any(BookEvent.class))).thenReturn(expected);

        this.mvc.perform(post("/books/66?action=BORROW&patronId=2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.reason", is(expected.getReason())));

        ArgumentCaptor<BookEvent> captor = ArgumentCaptor.forClass(BookEvent.class);
        Mockito.verify(this.libraryBookService).processBookEvent(captor.capture());

        BookEvent arg = captor.getValue();
        assertNotNull(arg);
        assertAll(
                () -> assertEquals(66, arg.getBookId(), "book id should match url path variable"),
                () -> assertEquals(BookEventType.BORROW, arg.getType(), "type should be BORROW"),
                () -> assertEquals(2, arg.getUserId(), "user id should match url query param")
        );
    }

}
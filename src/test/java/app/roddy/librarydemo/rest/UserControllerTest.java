package app.roddy.librarydemo.rest;

import app.roddy.librarydemo.DataService;
import app.roddy.librarydemo.database.DbBook;
import app.roddy.librarydemo.database.DbUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DataService dataService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(this.dataService);
    }

    @Test
    void testGetUserDetails() throws Exception {
        DbUser user = new DbUser();
        user.setId(66);
        user.setName("Test User");
        user.setJoinedOn(OffsetDateTime.now().minusDays(71));

        DbBook book1= new DbBook(1, "Test Book 1", "Test Author 1", "This is the first test book", 2021, user, OffsetDateTime.now().minusDays(1));
        DbBook book2 = new DbBook(2, "Test Book 2", "Test Author 2", "This is the second test book", 1999, user, OffsetDateTime.now().minusDays(10));
        List<DbBook> books = Arrays.asList(book1, book2);
        user.setCheckedOutBooks(books);
        Mockito.when(this.dataService.getUserById(Mockito.anyInt())).thenReturn(user);

        this.mvc.perform(get("/users/66"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.joinDate", is(user.getJoinedOn().toString())))
                .andExpect(jsonPath("$.checkedOutBooks", is(not(empty()))))

                .andExpect(jsonPath("$.checkedOutBooks[0].id", is(book1.getId())))
                .andExpect(jsonPath("$.checkedOutBooks[0].title", is(book1.getTitle())))
                .andExpect(jsonPath("$.checkedOutBooks[0].author", is(book1.getAuthor())))
                .andExpect(jsonPath("$.checkedOutBooks[0].dueOn", is(calculateDueDate(book1.getBorrowedOn()))))
                .andExpect(jsonPath("$.checkedOutBooks[0].isOverdue", is(false)))

                .andExpect(jsonPath("$.checkedOutBooks[1].id", is(book2.getId())))
                .andExpect(jsonPath("$.checkedOutBooks[1].title", is(book2.getTitle())))
                .andExpect(jsonPath("$.checkedOutBooks[1].author", is(book2.getAuthor())))
                .andExpect(jsonPath("$.checkedOutBooks[1].dueOn", is(calculateDueDate(book2.getBorrowedOn()))))
                .andExpect(jsonPath("$.checkedOutBooks[1].isOverdue", is(true)));

        Mockito.verify(this.dataService, Mockito.times(1)).getUserById(66);
    }

    private String calculateDueDate(OffsetDateTime borrowedOn) {
        return borrowedOn.withHour(12)
                .withMinute(0)
                .withSecond(0)
                .plusDays(7).toString();
    }
}
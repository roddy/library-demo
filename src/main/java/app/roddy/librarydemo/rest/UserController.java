package app.roddy.librarydemo.rest;

import app.roddy.librarydemo.DataService;
import app.roddy.librarydemo.database.DbUser;
import app.roddy.librarydemo.rest.models.UserBookDetails;
import app.roddy.librarydemo.rest.models.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final DataService dataService;

    public UserController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDetails getUserDetails(@PathVariable Integer id) {
        DbUser user = this.dataService.getUserById(id);

        List<UserBookDetails> books = user.getCheckedOutBooks().stream()
                .map(b -> {
                    OffsetDateTime overdueAfter = b.getBorrowedOn().withHour(12).withMinute(0).withSecond(0).plusDays(7);
                    return new UserBookDetails(b.getId(), b.getTitle(), b.getAuthor(), overdueAfter, OffsetDateTime.now().isAfter(overdueAfter));
                })
                .collect(Collectors.toList());

        return new UserDetails(
                user.getName(),
                user.getJoinedOn(),
                books
        );
    }
}

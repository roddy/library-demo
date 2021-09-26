package app.roddy.librarydemo.rest.models;

import java.time.OffsetDateTime;
import java.util.List;

public record UserDetails(
        String name,
        OffsetDateTime joinDate,
        List<UserBookDetails> checkedOutBooks
) {
}

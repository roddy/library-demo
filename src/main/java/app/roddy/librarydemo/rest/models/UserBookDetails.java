package app.roddy.librarydemo.rest.models;

import java.time.OffsetDateTime;

public record UserBookDetails(int id, String title, String author, OffsetDateTime dueOn, Boolean isOverdue) {
}
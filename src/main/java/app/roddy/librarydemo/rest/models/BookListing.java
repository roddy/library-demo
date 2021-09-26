package app.roddy.librarydemo.rest.models;

import java.util.List;

public record BookListing(int checkedOut, int available, int total, List<BookListingItem> books) {
}

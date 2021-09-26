package app.roddy.librarydemo.rest.models;

public record BookListingItem(int id, String title, String author, Boolean checkedOut) {
}
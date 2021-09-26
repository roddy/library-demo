package app.roddy.librarydemo.rest.models;

public record BookDetails(
        String title,
        String author,
        String description,
        int year,
        boolean checkedOut
) {}
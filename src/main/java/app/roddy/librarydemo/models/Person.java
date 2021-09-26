package app.roddy.librarydemo.models;

import java.util.List;

public class Person {
    private int id;
    private List<CheckedOutBook> checkedOutBooks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CheckedOutBook> getCheckedOutBooks() {
        return checkedOutBooks;
    }

    public void setCheckedOutBooks(List<CheckedOutBook> checkedOutBooks) {
        this.checkedOutBooks = checkedOutBooks;
    }
}

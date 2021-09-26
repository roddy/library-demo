package app.roddy.librarydemo.models;

public class BookEvent {
    BookEventType type; // required
    Integer bookId; // required
    Integer userId; // optional

    public BookEventType getType() {
        return type;
    }

    public void setType(BookEventType type) {
        this.type = type;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

package app.roddy.librarydemo.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "books")
public class DbBook {

    @Id
    private Integer id;

    private String title;
    private String author;
    private String description;
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "borrowed_by")
    private DbUser borrowedBy;

    @Column(name = "borrowed_on")
    private OffsetDateTime borrowedOn;

    public DbBook() {}

    public DbBook(Integer id, String title, String author, String description, Integer year, DbUser borrowedBy, OffsetDateTime borrowedOn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.year = year;
        this.borrowedBy = borrowedBy;
        this.borrowedOn = borrowedOn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public DbUser getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(DbUser borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public OffsetDateTime getBorrowedOn() {
        return borrowedOn;
    }

    public void setBorrowedOn(OffsetDateTime borrowedOn) {
        this.borrowedOn = borrowedOn;
    }
}

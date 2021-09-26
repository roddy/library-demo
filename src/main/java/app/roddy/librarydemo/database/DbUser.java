package app.roddy.librarydemo.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table( name="users" )
public class DbUser {

    @Id
    private Integer id;
    private String name;

    @Column(name = "joined_on")
    private OffsetDateTime joinedOn;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "borrowedBy")
    private List<DbBook> checkedOutBooks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(OffsetDateTime joinedOn) {
        this.joinedOn = joinedOn;
    }

    public List<DbBook> getCheckedOutBooks() {
        return checkedOutBooks;
    }

    public void setCheckedOutBooks(List<DbBook> checkedOutBooks) {
        this.checkedOutBooks = checkedOutBooks;
    }
}

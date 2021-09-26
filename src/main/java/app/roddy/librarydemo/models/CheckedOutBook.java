package app.roddy.librarydemo.models;

import java.time.OffsetDateTime;

public class CheckedOutBook {
    private int id;
    private OffsetDateTime checkedOutOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OffsetDateTime getCheckedOutOn() {
        return checkedOutOn;
    }

    public void setCheckedOutOn(OffsetDateTime checkedOutOn) {
        this.checkedOutOn = checkedOutOn;
    }
}

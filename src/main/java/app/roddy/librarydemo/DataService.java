package app.roddy.librarydemo;

import app.roddy.librarydemo.database.BookRepository;
import app.roddy.librarydemo.database.DbBook;
import app.roddy.librarydemo.database.DbUser;
import app.roddy.librarydemo.database.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DataService {

    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public DataService(BookRepository bookRepo, UserRepository userRepo) {
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public List<DbBook> getAllBooks() {
        Iterable<DbBook> results = this.bookRepo.findAll();

        Iterator<DbBook> iterator = results.iterator();
        List<DbBook> books = new ArrayList<>();
        while(iterator.hasNext()) {
            books.add(iterator.next());
        }
        return books;
    }

    @Transactional
    public DbBook getBookById(Integer id) {
        if (id == null) {
            return null;
        }

        return this.bookRepo.findById(id).orElse(null);
    }

    @Transactional
    public DbUser getUserById(Integer id) {
        if (id == null) {
            return null;
        }

        return this.userRepo.findById(id).orElse(null);
    }

    @Transactional
    public void saveBook(DbBook book) {
        this.bookRepo.save(book);
    }
}

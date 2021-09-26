package app.roddy.librarydemo.database;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<DbBook, Integer> {
}

package app.roddy.librarydemo.database;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<DbUser, Integer> {
}

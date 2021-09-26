# library-demo

This is a demo application for a library book-borrowing application using Drools. This application is intended to show
how you might go about having rules and a database interact without actually calling the database from the rules 
directly.

This is a Spring Boot application, with a REST HTTP interface and a h2 database. It's clearly not production ready, 
because H2 is not something you'd want to ever stick in production, but it's sufficient for the purpose of this 
demonstration.

This application uses Maven for dependency management. If you don't have Maven installed, we provide a local maven 
wrapper instance (`mvnw`) that you can use instead.

This project uses **Java 17**. It uses Java records, which were added in JDK 14 as an experiment; you can down-rev this 
project to any version of the JDK that supports Records. If you wish to down-rev further (eg. to Java 11 or 8), you'll 
need to rewrite the records to be simple "POJO"-type classes with getters and setters.  

## Build and run

To build and run this application from the commandline:

```
./mvnw spring-boot:run
```

The REST api will be available at http://localhost:8080/ .

## What is this?

This is a tiny application which models a library book checkout/return. There are ten books in the system, and three 
registered users.

Our library patrons are:

| ID | Name           | Description / Use Case              |
|----|----------------|-------------------------------------|
| 1  | Jane Q. Public | Regular user, no books checked out. |
| 2  | Oliver Overdue | 1 book checked out, very overdue.   |
| 3  | Ryan Reader    | 3 books checked out, none overdue.  |

With these patrons, we can try to do a variety of things with regards to our books. The following endpoints are 
available:

| Verb | Endpoint      | Description                                                                                                  |
|------|---------------|--------------------------------------------------------------------------------------------------------------|
| GET  | `/books`      | Get all books owned by the library, along with checked in/checked out indicator.                             |
| GET  | `/books/{id}` | Get a specific book's details. Shows some basic info about the book (title, author, etc.)                    |
| POST | `/books/{id}` | Query Params: `action=BORROW` or `action=RETURN` and `patronId={userId}`. Either check out or return a book. |
| GET  | `/users/{id}` | Get some information about the specified patron, including what books they've checked out.                   |

To borrow a book:
```
POST /books/{id}?patronId=1&action=BORROW
```

To return the book:
```
POST /books/{id}?patronId=1&action=RETURN
```

There is an in-memory H2 database instance which is tracking books and users.

## Use cases

We've modelled the following use cases in the rules:

* If a user has 3 books already checked out, no more books are allowed to be checked out. Any attempts to check out 
  additional books should be denied.
* If a user has a book checked out for more than 7 days, that book is overdue. Any attempts to check out additional 
  books should be denied.
* If a user has no overdue books, and fewer than 3 books checked out, they should be allowed to check out additional 
  books.
* If an unrecognized user attempts to check out a book, they should be denied. Only the currently registered patrons are
  allowed to check out books.

You can find the rules at [src/main/resources/rules/library-books.drl](./src/main/resources/rules/library-books.drl).

There is minimal error handling for complex use cases -- for example, if user 1 has checked out a book, nothing is 
stopping user 2 from checking out the same book. On the one hand, it's technically a logic flaw ... but at the same time
it's technically modelling a use case where a book somehow made its way back onto the shelf without being properly
scanned and accounted for.

To see where we invoke the rules, refer to the 
[LibraryBookService](./src/main/java/app/roddy/librarydemo/LibraryBookService.java) class.

## Dependency version information

* Maven 3+
* Spring-Boot BOM 2.5.5 (Data-JPA, Web, Actuator)
* Drools 7.59.0.Final (Classic, with mvel)
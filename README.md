# Library-management (JavaFx+SQlite)

This project is a simple library management system developed using JavaFX and SQLite.


Features:
1. Member Side ->
Register and log in,
View and update profile information (phone, email, address),
List all books and search by title, author, category, or ISBN,
Borrow available books (loan date + 14-day due date),
View active and past loans on the "My Books" screen.


2. Librarian Side ->
Dashboard: total books, members, active loans, and overdue counts,
Books Management: add, delete, update books,
Members Management: view, search, and edit member information,
Loan Issuing: select a member and a book to issue a loan,
Top Screen: top 5 most used authors and categories,
Issued/Overdue Screen: overdue books and related member details.


Technologies Used ->
Java 25,
JavaFX 25,
Maven,
SQLite (JDBC: org.xerial:sqlite-jdbc).


How to Run ->
Make sure JDK and Maven are installed,
Clone the project,
Build with Maven,
Open the project in IntelliJ IDEA and configure JavaFX VM options (if not using a JavaFX plugin).


Database ->
By default, the library.db file is located in the project root. Use db/schema.sql for the database schema. Use db/sample_data.sql for sample data



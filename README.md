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


## Screenshots

**Login view**

<img width="731" height="508" alt="Screenshot (2)" src="https://github.com/user-attachments/assets/03e537c8-d94f-40bc-a43d-445798a89a2d" /> <img width="739" height="522" alt="Screenshot (3)" src="https://github.com/user-attachments/assets/6febe3c7-7188-468f-be67-507d0e175707" />

**Librarian views**

<img width="1104" height="766" alt="Screenshot (5)" src="https://github.com/user-attachments/assets/674041a7-db7b-49d5-be7b-1efa64e9496a" /> 
<img width="1120" height="769" alt="Screenshot (6)" src="https://github.com/user-attachments/assets/6238e37f-6160-47b8-a84a-c38eaca37027" />
<img width="1109" height="776" alt="Screenshot (7)" src="https://github.com/user-attachments/assets/b0444c20-85e4-4cfa-bd71-e536f343aa27" />
<img width="1110" height="769" alt="Screenshot (8)" src="https://github.com/user-attachments/assets/1f397146-39e3-49a7-ad0f-a4def3f42ffb" />
<img width="1111" height="766" alt="Screenshot (9)" src="https://github.com/user-attachments/assets/917ee7bc-0895-4432-ab90-9f87a9267981" />
<img width="1114" height="769" alt="Screenshot (10)" src="https://github.com/user-attachments/assets/3163ab1e-7abd-4c26-beac-e1b995142fe4" />

**Member views**

<img width="1107" height="766" alt="Screenshot (11)" src="https://github.com/user-attachments/assets/bb1ee92f-42a1-4159-b4b2-595fe84beae1" />
<img width="1115" height="769" alt="Screenshot (12)" src="https://github.com/user-attachments/assets/b1a83d83-0019-45dc-b09d-9e42fd184a08" />
<img width="1115" height="773" alt="Screenshot (13)" src="https://github.com/user-attachments/assets/fcd7ea2f-f76d-4ee2-8d2b-3f64dba99f57" />



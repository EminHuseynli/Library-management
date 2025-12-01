package library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.dao.BookDao;
import library.dao.LibrarianDao;
import library.dao.LoanDao;
import library.dao.MemberDao;
import library.model.Librarian;

import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initDatabase();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();

            primaryStage.setTitle("Library Management System - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        try {
            BookDao bookDao = new BookDao();
            MemberDao memberDao = new MemberDao();
            LibrarianDao librarianDao = new LibrarianDao();
            LoanDao loanDao = new LoanDao();

            bookDao.createTableIfNotExists();
            memberDao.createTableIfNotExists();
            librarianDao.createTableIfNotExists();
            loanDao.createTableIfNotExists();

            if (!librarianDao.existsByUsername("admin")) {
                Librarian admin = new Librarian("admin", "admin123");
                librarianDao.insertLibrarian(admin);
                System.out.println("Default librarian created: admin / admin123");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

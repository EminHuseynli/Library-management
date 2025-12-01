package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.dao.BookDao;
import library.dao.LoanDao;
import library.model.Book;
import library.model.Loan;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class LibrarianTopController {

    // Top authors table
    @FXML
    private TableView<TopItem> topAuthorsTable;

    @FXML
    private TableColumn<TopItem, String> authorNameColumn;

    @FXML
    private TableColumn<TopItem, Integer> authorCountColumn;

    // Top categories table
    @FXML
    private TableView<TopItem> topCategoriesTable;

    @FXML
    private TableColumn<TopItem, String> categoryNameColumn;

    @FXML
    private TableColumn<TopItem, Integer> categoryCountColumn;

    private final ObservableList<TopItem> topAuthorsList = FXCollections.observableArrayList();
    private final ObservableList<TopItem> topCategoriesList = FXCollections.observableArrayList();

    private final LoanDao loanDao = new LoanDao();
    private final BookDao bookDao = new BookDao();

    @FXML
    public void initialize() {
        authorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        topAuthorsTable.setItems(topAuthorsList);
        topCategoriesTable.setItems(topCategoriesList);

        loadTopStats();
    }

    private void loadTopStats() {
        topAuthorsList.clear();
        topCategoriesList.clear();

        try {
            List<Loan> loans = loanDao.getAllLoans();

            Map<String, Integer> authorCounts = new HashMap<>();
            Map<String, Integer> categoryCounts = new HashMap<>();

            for (Loan loan : loans) {
                Book book = bookDao.getBookById(loan.getBookId());
                if (book == null) continue;

                String author = safeString(book.getAuthor(), "Unknown");
                String category = safeString(book.getCategory(), "Unknown");

                authorCounts.put(author, authorCounts.getOrDefault(author, 0) + 1);
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }

            List<TopItem> topAuthors = authorCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(e -> new TopItem(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            List<TopItem> topCategories = categoryCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(e -> new TopItem(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            topAuthorsList.setAll(topAuthors);
            topCategoriesList.setAll(topCategories);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String safeString(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    public static class TopItem {
        private final String name;
        private final int count;

        public TopItem(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }
    }
}



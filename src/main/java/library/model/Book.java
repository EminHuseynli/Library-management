
package library.model;

public class Book {

    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int copyCount;

    public Book() {
    }

    public Book(int id, String title, String author, String isbn, String category, int copyCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.copyCount = copyCount;
    }

    public Book(String title, String author, String isbn, String category, int copyCount) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.copyCount = copyCount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", category='" + category + '\'' +
                ", copyCount=" + copyCount +
                '}';
    }
}

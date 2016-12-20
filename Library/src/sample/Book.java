package sample;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by alaashammaa on 11/10/16.
 */
public class Book {
    SimpleStringProperty ISBN;
    SimpleStringProperty authorIDs;
    SimpleStringProperty title;
    SimpleStringProperty year;
    SimpleStringProperty publisher;
    SimpleStringProperty authors;

    public Book() {
    }

    public Book(String ISBN, String authorIDs, String title, String year, String publisher,String authors) {
        this.ISBN = new SimpleStringProperty(ISBN);
        this.authorIDs = new SimpleStringProperty(authorIDs);
        this.title = new SimpleStringProperty(title);
        this.year = new SimpleStringProperty(year);
        this.publisher = new SimpleStringProperty(publisher);
        this.authors = new SimpleStringProperty(authors);
    }

    public String getAuthors() {
        return authors.get();
    }

    public void setAuthors(String authors) {
        this.authors.set(authors);
    }

    public String getISBN() {
        return ISBN.get();
    }

    public void setISBN(String ISBN) {
        this.ISBN.set(ISBN);
    }

    public String getAuthorIDs() {
        return authorIDs.get();
    }

    public void setAuthorIDs(String authorIDs) {
        this.authorIDs.set(authorIDs);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public String getPublisher() {
        return publisher.get();
    }

    public void setPublisher(String publisher) {
        this.publisher.set(publisher);
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN=" + ISBN +
                ", authorIDs=" + authorIDs +
                ", title=" + title +
                ", year=" + year +
                ", publisher=" + publisher +
                '}';
    }
}

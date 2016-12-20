package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        File inOutFile = new File("src/sample/library.xml");
        SAXReader reader = new SAXReader();
        Document document = reader.read(inOutFile);
        Element xmlRoot = document.getRootElement();
        if (xmlRoot.nodeCount() < 2) {
            List<Author> authors = new ArrayList<>();
            List<Book> books = new ArrayList<>();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library", "root", "2651990");
            Statement stmt = con.createStatement();
            String query = "select * from author";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String nationality = rs.getString("nationality");
                Author author = new Author(String.valueOf(id), firstName, middleName, lastName, nationality);
                authors.add(author);
            }
            query = "SELECT * FROM book";
            Statement statement = con.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                boolean pass = true;
                String authorsQuery = "SELECT author_id from book where isbn='%s'";
                String isbn = rs.getString("isbn");
                for (Book book : books) {
                    if (Objects.equals(book.getISBN(), isbn)) {
                        pass = false;
                    }
                }
                if (pass) {
                    String formattedAuthorsQuery = String.format(authorsQuery, isbn);
                    String title = rs.getString("title");
                    String year = rs.getString("year");
                    String publisher = rs.getString("publisher");
                    ResultSet resultSet = stmt.executeQuery(formattedAuthorsQuery);
                    StringBuilder authorsId = new StringBuilder();
                    while (resultSet.next()) {
                        String a_Id = resultSet.getString("author_id");
                        authorsId.append(a_Id);
                        authorsId.append(",");
                    }
                    authorsId.deleteCharAt(authorsId.length() - 1);
                    Book book = new Book(isbn, authorsId.toString(), title, year, publisher, null);
                    books.add(book);
                }
            }
            for (Author a : authors) {
                Element authorElement = xmlRoot.addElement("author")
                        .addAttribute("id", a.getId());
                authorElement.addElement("firstName")
                        .addText(a.getFirstName());
                if (a.getMiddleName() != null) {
                    authorElement.addElement("middleName")
                            .addText(a.getMiddleName());
                }
                authorElement.addElement("lastName")
                        .addText(a.getLastName());
                if (a.getNationality() != null) {
                    authorElement.addElement("nationality")
                            .addText(a.getNationality());
                }
            }
            for (Book book : books) {
                Element bookElement = xmlRoot.addElement("book")
                        .addAttribute("ISBN", book.getISBN())
                        .addAttribute("authors", book.getAuthorIDs());
                bookElement.addElement("title")
                        .addText(book.getTitle());
                if (book.getYear() != null) {
                    bookElement.addElement("year")
                            .addText(book.getYear());
                }
                if (book.getPublisher() != null) {
                    bookElement.addElement("publisher")
                            .addText(book.getPublisher());
                }
            }
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            con.close();
        }
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Parent addEditBooks = FXMLLoader.load(getClass().getResource("Add_EditBooks.fxml"));
        Parent addEditAuthors = FXMLLoader.load(getClass().getResource("addEditAuthors.fxml"));
        Parent search = FXMLLoader.load(getClass().getResource("Search.fxml"));
        Parent searchAuthors = FXMLLoader.load(getClass().getResource("searchAuthors.fxml"));
        Parent searchBooks = FXMLLoader.load(getClass().getResource("searchBooks.fxml"));
        Scene homeScene = new Scene(root, 600, 450);
        Scene addEditBooksScene = new Scene(addEditBooks, 600, 450);
        Scene searchBooksScene = new Scene(searchBooks, 600, 450);
        Scene searchAuthorsScene = new Scene(searchAuthors, 600, 450);
        Scene addEditAuthorsScene = new Scene(addEditAuthors, 600, 450);
        Scene searchScene = new Scene(search, 600, 450);
        String css = this.getClass().getResource("/sample/styles.css").toExternalForm();
        addEditBooksScene.getStylesheets().add(css);
        addEditAuthorsScene.getStylesheets().add(css);
        searchAuthorsScene.getStylesheets().add(css);
        searchBooksScene.getStylesheets().add(css);
        primaryStage.setTitle("Library");
        primaryStage.setResizable(false);
        primaryStage.setScene(homeScene);
        ImageView home_nav_books = (ImageView) addEditBooks.lookup("#home_nav");
        ImageView home_nav_authors = (ImageView) addEditAuthors.lookup("#home_nav");
        ImageView home_nav_Searchauthors = (ImageView) searchAuthors.lookup("#home_nav");
        ImageView home_nav_Searchbooks = (ImageView) searchBooks.lookup("#home_nav");
        Button addEditBook = (Button) root.lookup("#addEditBooks");
        Button searchButton = (Button) root.lookup("#searchBooksAuthors");
        Button addEditAuthor = (Button) root.lookup("#addEditAuthors");
        Button searchBooksBTN = (Button) search.lookup("#searchBooks");
        Button searchAuthorsBTN = (Button) search.lookup("#searchAuthors");
        Button returnBTN = (Button) search.lookup("#return");
        returnBTN.setStyle("-fx-base: #6D4C41;");
        ImageView books = new ImageView("/images/Books.png");
        ImageView uni = new ImageView("/images/Ink.png");
        ImageView addEditBookIV = new ImageView("/images/library.png");
        addEditBookIV.setFitWidth(100);
        addEditBookIV.setFitHeight(100);
        addEditBookIV.setPreserveRatio(true);
        ImageView searchIV = new ImageView("/images/search.png");
        searchIV.setFitWidth(100);
        searchIV.setFitHeight(100);
        searchIV.setPreserveRatio(true);
        ImageView addEditAuthorIV = new ImageView("/images/writer.png");
        addEditAuthorIV.setFitWidth(100);
        addEditAuthorIV.setFitHeight(100);
        addEditAuthorIV.setPreserveRatio(true);
        addEditBook.setGraphic(books);
        addEditBook.setStyle("-fx-base: #8D6E63;");
        addEditBook.setContentDisplay(ContentDisplay.TOP);
        addEditAuthor.setGraphic(uni);
        addEditAuthor.setStyle("-fx-base: #8D6E63;");
        addEditAuthor.setContentDisplay(ContentDisplay.TOP);
        searchButton.setGraphic(searchIV);
        searchButton.setStyle("-fx-base: #8D6E63;");
        searchButton.setContentDisplay(ContentDisplay.TOP);
        searchBooksBTN.setGraphic(addEditBookIV);
        searchBooksBTN.setStyle("-fx-base: #8D6E63;");
        searchBooksBTN.setContentDisplay(ContentDisplay.TOP);
        searchAuthorsBTN.setGraphic(addEditAuthorIV);
        searchAuthorsBTN.setStyle("-fx-base: #8D6E63;");
        searchAuthorsBTN.setContentDisplay(ContentDisplay.TOP);
        addEditBook.setOnAction(event -> {
            primaryStage.setScene(addEditBooksScene);
        });
        addEditAuthor.setOnAction(event -> {
            primaryStage.setScene(addEditAuthorsScene);
        });
        searchButton.setOnAction(event -> {
            primaryStage.setScene(searchScene);
        });
        searchBooksBTN.setOnAction(event -> {
            primaryStage.setScene(searchBooksScene);
        });
        searchAuthorsBTN.setOnAction(event -> {
            primaryStage.setScene(searchAuthorsScene);
        });
        home_nav_books.setOnMouseClicked(event -> {
            primaryStage.setScene(homeScene);
        });
        home_nav_authors.setOnMouseClicked(event -> {
            primaryStage.setScene(homeScene);
        });
        home_nav_Searchauthors.setOnMouseClicked(event -> {
            primaryStage.setScene(searchScene);
        });
        home_nav_Searchbooks.setOnMouseClicked(event -> {
            primaryStage.setScene(searchScene);
        });
        returnBTN.setOnAction(event -> {
            primaryStage.setScene(homeScene);
        });
        addEditBooksScene.getStylesheets().add("sample/styles.css");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 * Created by alaa on 11/26/16.
 */
public class searchBooksController {
    public CheckBox cbISBN;
    public CheckBox cbTitle;
    public CheckBox cbID;
    public CheckBox cbLastName;
    private ObservableList<Book> booksData;
    private ObservableList<String> authorsNameData;
    private ObservableList<Author> authorsData;
    Document document;
    @FXML
    private TableView<Author> authorsTable;
    @FXML
    CheckBox nationalityCB;
    @FXML
    TextField searchField;
    @FXML
    private TableColumn<Author, String> idCol;
    @FXML
    private TableColumn<Author, String> fNameCol;
    @FXML
    private TableColumn<Author, String> mNameCol;
    @FXML
    private TableColumn<Author, String> lNameCol;
    @FXML
    private TableColumn<Author, String> nationalityCol;
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, String> isbnCol;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> yearCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, String> authorsCol;
    Element xmlRoot;
    @FXML
    private Group cbGroup;

    public void initialize() throws DocumentException {
        booksData = FXCollections.observableArrayList();
        authorsData = FXCollections.observableArrayList();
        isbnCol.setCellValueFactory(new PropertyValueFactory<Book, String>("ISBN"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        yearCol.setCellValueFactory(new PropertyValueFactory<Book, String>("year"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<Book, String>("publisher"));
        authorsCol.setCellValueFactory(new PropertyValueFactory<Book, String>("authors"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        mNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nationalityCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        cbISBN.selectedProperty().addListener(new CheckBoxChangeListener(this.cbISBN));
        cbID.selectedProperty().addListener(new CheckBoxChangeListener(this.cbID));
        cbTitle.selectedProperty().addListener(new CheckBoxChangeListener(this.cbTitle));
        cbLastName.selectedProperty().addListener(new CheckBoxChangeListener(this.cbLastName));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            booksData.clear();
            authorsData.clear();
        });
    }

    public void handleSearchBTN(ActionEvent actionEvent) throws DocumentException {
        File inOutFile = new File("src/sample/library.xml");
        SAXReader reader = new SAXReader();
        document = reader.read(inOutFile);
        xmlRoot = document.getRootElement();
        if (searchField.getText().isEmpty()) {
            return;
        }
        String isbn = null;
        if (cbISBN.isSelected() || cbTitle.isSelected()) {
            if (cbTitle.isSelected()) {
                String title = searchField.getText();
                List<org.dom4j.Node> bookNodes = document.selectNodes("library/book");
                for (org.dom4j.Node book : bookNodes) {
                    if (book.selectSingleNode("title").getText().contains(title)) {
                        isbn = book.valueOf("@ISBN");
                        break;
                    }
                }
            } else {
                isbn = searchField.getText();
            }
            String xPathExpression = String.format("/library/book[@ISBN='%s']", isbn);
            org.dom4j.Node book = document.selectSingleNode(xPathExpression);
            if (book == null) {
                booksData.clear();
                authorsData.clear();
                // TODO: Alert the user
            } else {
                booksData = addBooks(booksData, book);
                String[] ids = booksData.get(0).getAuthorIDs().split(",");
                for (String id : ids) {
                    String xPath = String.format("/library/author[@id='%s']", id);
                    org.dom4j.Node author = document.selectSingleNode(xPath);
                    String aID = null;
                    String fName = null;
                    String lName = null;
                    String nat = null;
                    String mName = null;
                    aID = author.valueOf("@id");
                    fName = author.selectSingleNode("firstName").getText();
                    if (author.selectSingleNode("middleName") != null) {
                        mName = author.selectSingleNode("middleName").getText();
                    }
                    lName = author.selectSingleNode("lastName").getText();
                    if (author.selectSingleNode("nationality") != null) {
                        nat = author.selectSingleNode("nationality").getText();
                    }
                    if (aID != null) {
                        Author a = new Author(aID, fName, mName, lName, nat);
                        authorsData.add(a);
                    }
                }
                authorsTable.setItems(authorsData);
                booksTable.setItems(booksData);
            }
        } else if (cbLastName.isSelected() || cbID.isSelected()) {
            String lastName = searchField.getText();
            String id = null;
            if (cbLastName.isSelected()) {
                List<org.dom4j.Node> authors = document.selectNodes("library/author");
                for (org.dom4j.Node author : authors) {
                    if (author.selectSingleNode("lastName").getText().contains(lastName)) {
                        id = author.valueOf("@id");
                        break;
                    }
                }
            } else {
                id = searchField.getText();
            }
            if (id == null) {
                System.out.println("Author Not Found");
                return;
            }
            List<org.dom4j.Node> bookNodes = document.selectNodes("library/book");
            for (org.dom4j.Node book : bookNodes) {
                if (book.valueOf("@authors").contains(id)) {
                    booksData = addBooks(booksData, book);
                }
            }
            booksTable.setItems(booksData);
        } else {
            // TODO: Alert the user with a message:
            // Please Select one of the checkboxes
        }
    }

    private ObservableList<Book> addBooks(ObservableList<Book> data, org.dom4j.Node book) {
        if (book.selectSingleNode("year") == null && book.selectSingleNode("publisher") == null) {
            Book b = new Book(book.valueOf("@ISBN"),
                    book.valueOf("@authors"),
                    book.selectSingleNode("title").getText(),
                    null,
                    null, null);
            String authors = cookAuthorsIDs(b);
            b.setAuthors(authors);
            data.add(b);
        } else {
            if (book.selectSingleNode("year") == null) {
                Book b = new Book(book.valueOf("@ISBN"),
                        book.valueOf("@authors"),
                        book.selectSingleNode("title").getText(),
                        null,
                        book.selectSingleNode("publisher").getText(), null);
                String authors = cookAuthorsIDs(b);
                b.setAuthors(authors);
                data.add(b);
            } else if (book.selectSingleNode("publisher") == null) {
                Book b = new Book(book.valueOf("@ISBN"),
                        book.valueOf("@authors"),
                        book.selectSingleNode("title").getText(),
                        book.selectSingleNode("year").getText(),
                        null
                        , null);
                String authors = cookAuthorsIDs(b);
                b.setAuthors(authors);
                data.add(b);
            }
        }
        return data;
    }

    private String cookAuthorsIDs(Book b) {
        StringBuilder authors = new StringBuilder();
        String[] arr = b.getAuthorIDs().split(",");
        for (String aID : arr) {
            String xPathExpression = String.format("/library/author[@id='%s']", aID);
            org.dom4j.Node author = document.selectSingleNode(xPathExpression);
            authors.append(author.selectSingleNode("firstName").getText());
            authors.append(" ");
            authors.append(author.selectSingleNode("lastName").getText());
            authors.append(",");
        }
        authors.deleteCharAt(authors.length() - 1);
        return authors.toString();
    }

    private class CheckBoxChangeListener implements ChangeListener {
        private final CheckBox checkBox;

        private CheckBoxChangeListener(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (checkBox.isSelected()) {
                booksData.clear();
                authorsData.clear();
                searchField.clear();
                ObservableList<Node> nodes = cbGroup.getChildren();
                for (Node node : nodes) {
                    if (node instanceof CheckBox && node != checkBox) {
                        ((CheckBox) node).setSelected(false);
                    }
                }
            }
        }
    }
}
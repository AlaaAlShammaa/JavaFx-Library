package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by alaa on 11/23/16.
 */
public class Add_EditBooksController {
    private ObservableList<Book> booksData;
    private ObservableList<String> authorsNameData;
    Document document;
    @FXML
    private Button addBook;
    @FXML
    private Button deleteBook;
    @FXML
    private TextField isbnTF;
    @FXML
    private TextField titleTF;
    @FXML
    private TextField yearTF;
    @FXML
    private TextField publisherTF;
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
    @FXML
    private GridPane booksGridPane;
    Validator<String> emptyTFValidator;
    Validator<String> duplicateISBNValidator;
    ValidationSupport support;
    CheckComboBox authorsCB;
    Element xmlRoot;

    public void initialize() throws DocumentException {
        support = new ValidationSupport();
        emptyTFValidator = new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String s) {
                boolean condition = s.isEmpty();
                return ValidationResult.fromMessageIf(control, "Can't Leave it empty.", Severity.ERROR, condition);
            }
        };
        duplicateISBNValidator = new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String isbn) {
                boolean condition = false;
                List<Node> books = document.selectNodes("library/book");
                for (Node b : books) {
                    if (b.valueOf("@ISBN").equals(isbn)) {
                        condition = true;
                        break;
                    }
                }
                return ValidationResult.fromMessageIf(control, "Found A Duplicate ISBN in the Library", Severity.ERROR, condition);
            }
        };
        File inOutFile = new File("src/sample/library.xml");
        SAXReader reader = new SAXReader();
        document = reader.read(inOutFile);
        xmlRoot = document.getRootElement();
        booksData = FXCollections.observableArrayList();
        authorsNameData = FXCollections.observableArrayList();
        authorsCB = new CheckComboBox(authorsNameData);
        authorsCB.setPadding(new Insets(0, 0, 5, 0));
        booksGridPane.add(authorsCB, 1, 4);
        isbnCol.setCellValueFactory(new PropertyValueFactory<Book, String>("ISBN"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        yearCol.setCellValueFactory(new PropertyValueFactory<Book, String>("year"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<Book, String>("publisher"));
        authorsCol.setCellValueFactory(new PropertyValueFactory<Book, String>("authors"));
        buildTableViewData();
        buildComboBoxData();
        support.registerValidator(isbnTF, true, emptyTFValidator);
        support.registerValidator(isbnTF, true, duplicateISBNValidator);
        support.registerValidator(titleTF, true, emptyTFValidator);
    }

    private void buildComboBoxData() {
        List<Node> authorNodes = document.selectNodes("library/author");
        for (Node author : authorNodes) {
            authorsNameData.add(author.selectSingleNode("firstName").getText() + " " + author.selectSingleNode("lastName").getText());
        }
    }

    public void buildTableViewData() {
        // read xml file into tableview
        List<Node> bookNodes = document.selectNodes("library/book");
        for (Node book : bookNodes) {
            if (book.selectSingleNode("year") == null && book.selectSingleNode("publisher") == null) {
                Book b = new Book(book.valueOf("@ISBN"),
                        book.valueOf("@authors"),
                        book.selectSingleNode("title").getText(),
                        null,
                        null, null);
                String authors = cookAuthorsIDs(b);
                b.setAuthors(authors);
                booksData.add(b);
            } else {
                if (book.selectSingleNode("year") == null) {
                    Book b = new Book(book.valueOf("@ISBN"),
                            book.valueOf("@authors"),
                            book.selectSingleNode("title").getText(),
                            null,
                            book.selectSingleNode("publisher").getText(), null);
                    String authors = cookAuthorsIDs(b);
                    b.setAuthors(authors);
                    booksData.add(b);
                } else if (book.selectSingleNode("publisher") == null) {
                    Book b = new Book(book.valueOf("@ISBN"),
                            book.valueOf("@authors"),
                            book.selectSingleNode("title").getText(),
                            book.selectSingleNode("year").getText(),
                            null
                            , null);
                    String authors = cookAuthorsIDs(b);
                    b.setAuthors(authors);
                    booksData.add(b);
                } else {
                    Book b = new Book(book.valueOf("@ISBN"),
                            book.valueOf("@authors"),
                            book.selectSingleNode("title").getText(),
                            book.selectSingleNode("year").getText(),
                            book.selectSingleNode("publisher").getText(), null);
                    String authors = cookAuthorsIDs(b);
                    b.setAuthors(authors);
                    booksData.add(b);

                }
            }
        }
        booksTable.setItems(booksData);
    }

    private String cookAuthorsIDs(Book b) {
        StringBuilder authors = new StringBuilder();
        String[] arr = b.getAuthorIDs().split(",");
        for (String aID : arr) {
            String xPathExpression = String.format("/library/author[@id='%s']", aID);
            Node author = document.selectSingleNode(xPathExpression);
            authors.append(author.selectSingleNode("firstName").getText());
            authors.append(" ");
            authors.append(author.selectSingleNode("lastName").getText());
            authors.append(",");
        }
        authors.deleteCharAt(authors.length() - 1);
        return authors.toString();
    }


    public void handleAddBook(ActionEvent actionEvent) throws IOException {
        if (support.isInvalid() || authorsCB.getCheckModel().getCheckedItems().size() == 0) {
            return;
        }
        StringBuilder authorIDs = new StringBuilder();
        ObservableList list = authorsCB.getCheckModel().getCheckedItems();
        for (Object id : list) {
            String[] arr = id.toString().split(" ");
            List<Node> authors = document.selectNodes("library/author");
            for (Node author : authors) {
                if (author.selectSingleNode("firstName").getText().equals(arr[0])
                        && author.selectSingleNode("lastName").getText().equals(arr[1])) {
                    authorIDs.append(author.valueOf("@id"));
                    authorIDs.append(",");
                    break;
                }
            }
        }
        authorIDs.deleteCharAt(authorIDs.length() - 1);
        if (yearTF.getText().isEmpty() && publisherTF.getText().isEmpty()) {
            Book book = new Book(isbnTF.getText(), authorIDs.toString(), titleTF.getText(), null, null, null);
            Element bookElement = xmlRoot.addElement("book")
                    .addAttribute("ISBN", book.getISBN());
            bookElement.addElement("title")
                    .addText(book.getTitle());
            bookElement.addAttribute("authors", book.getAuthorIDs());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            isbnTF.clear();
            titleTF.clear();
            booksData.clear();
            buildTableViewData();
        } else if (!yearTF.getText().isEmpty() && publisherTF.getText().isEmpty()) {
            Book book = new Book(isbnTF.getText(), authorIDs.toString(), titleTF.getText(), yearTF.getText(), null, null);
            Element bookElement = xmlRoot.addElement("book")
                    .addAttribute("ISBN", book.getISBN());
            bookElement.addElement("title")
                    .addText(book.getTitle());
            bookElement.addElement("year")
                    .addText(book.getYear());
            bookElement.addAttribute("authors", book.getAuthorIDs());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            isbnTF.clear();
            titleTF.clear();
            publisherTF.clear();
            booksData.clear();
            buildTableViewData();
        } else {
            Book book = new Book(isbnTF.getText(), authorIDs.toString(), titleTF.getText(), yearTF.getText(), publisherTF.getText(), null);
            Element bookElement = xmlRoot.addElement("book")
                    .addAttribute("ISBN", book.getISBN());
            bookElement.addElement("title")
                    .addText(book.getTitle());
            bookElement.addElement("year")
                    .addText(book.getYear());
            bookElement.addElement("publisher")
                    .addText(book.getPublisher());
            bookElement.addAttribute("authors", book.getAuthorIDs());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            isbnTF.clear();
            titleTF.clear();
            yearTF.clear();
            publisherTF.clear();
            booksData.clear();
            buildTableViewData();
        }
    }

    public void handleDeleteBook(ActionEvent actionEvent) throws IOException {
        if (isNullOrWhiteSpace(isbnTF.getText())) {
            return;
        }
        String isbn = isbnTF.getText();
        String xPathExpression = "/library/book[@ISBN='%s']";
        xPathExpression = String.format(xPathExpression, isbn);
        Node node = document.selectSingleNode(xPathExpression);
        if (node == null) {
            System.out.println("Book Not Found");
        } else {
            node.detach();
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            System.out.println("Book's been deleted");
        }
        isbnTF.clear();
        titleTF.clear();
        yearTF.clear();
        publisherTF.clear();
        booksData.clear();
        buildTableViewData();
    }

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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
import java.util.Objects;
import java.util.Random;

/**
 * Created by alaa on 11/23/16.
 */
public class addEditAuthorsController {

    private ObservableList<Author> authorsData;
    Document document;
    @FXML
    private Button addAuthor;
    @FXML
    private Button deleteAuthor;
    @FXML
    private TextField idTF;
    @FXML
    private TextField fNameTF;
    @FXML
    private TextField lNameTF;
    @FXML
    private TextField mNameTF;
    @FXML
    private TextField nationalityTF;
    @FXML
    private TableView<Author> authorsTable;
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
    private ImageView home_nav;
    Validator<String> emptyTFValidator;
    Validator<String> duplicateIDValidator;
    ValidationSupport support;
    Element xmlRoot;
    public void initialize() throws DocumentException {
        support = new ValidationSupport();
        emptyTFValidator = new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String s) {
                boolean condition = isNullOrWhiteSpace(s);
                return ValidationResult.fromMessageIf(control, "Can't Leave it empty.", Severity.ERROR, condition);
            }
        };
        duplicateIDValidator = new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String id) {
                boolean condition = false;
                List<Node> authors = document.selectNodes("library/author");
                for (Node a : authors) {
                    if (a.valueOf("@id").equals(id)) {
                        condition = true;
                        break;
                    }
                }
                return ValidationResult.fromMessageIf(control, "Found A Duplicate Author ID in the Library", Severity.ERROR, condition);
            }
        };
        File inOutFile = new File("src/sample/library.xml");
        SAXReader reader = new SAXReader();
        document = reader.read(inOutFile);
        xmlRoot = document.getRootElement();
        authorsData = FXCollections.observableArrayList();
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        mNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nationalityCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        buildTableViewData();
        support.registerValidator(idTF, true, duplicateIDValidator);
        support.registerValidator(fNameTF, true, emptyTFValidator);
        support.registerValidator(lNameTF, true, emptyTFValidator);
        generateRandomID();
    }

    private void clearFields() {
        idTF.clear();
        fNameTF.clear();
        lNameTF.clear();
        mNameTF.clear();

    }

    private void generateRandomID() {
        Random random = new Random(20);
        List<Node> authors = document.selectNodes("library/author");
        boolean duplicate = false;
        int aId;
        int c = 0;
        do {
            aId = random.nextInt(200) + 1;
            for (Node a : authors) {
                if (Objects.equals(String.valueOf(aId), a.valueOf("@id"))) {
                    duplicate = true;
                    c = 0;
                }
                c++;
                if (c == authors.size() - 1) {
                    duplicate = false;
                }
            }
        } while (duplicate);
        idTF.setText(String.valueOf(aId));
    }

    private void buildTableViewData() {
        List<Node> authorNodes = document.selectNodes("library/author");
        for (Node author : authorNodes) {
            if (author.selectSingleNode("middleName") != null && author.selectSingleNode("nationality") != null) {
                Author a = new Author(author.valueOf("@id")
                        , author.selectSingleNode("firstName").getText()
                        , author.selectSingleNode("middleName").getText(),
                        author.selectSingleNode("lastName").getText()
                        , author.selectSingleNode("nationality").getText());
                authorsData.add(a);
            } else if (author.selectSingleNode("middleName") == null && author.selectSingleNode("nationality") != null) {
                Author a = new Author(author.valueOf("@id")
                        , author.selectSingleNode("firstName").getText()
                        , null,
                        author.selectSingleNode("lastName").getText()
                        , author.selectSingleNode("nationality").getText());
                authorsData.add(a);
            } else if (author.selectSingleNode("middleName") != null && author.selectSingleNode("nationality") == null) {
                Author a = new Author(author.valueOf("@id")
                        , author.selectSingleNode("firstName").getText()
                        , author.selectSingleNode("middleName").getText(),
                        author.selectSingleNode("lastName").getText()
                        , null);
                authorsData.add(a);
            } else {
                Author a = new Author(author.valueOf("@id")
                        , author.selectSingleNode("firstName").getText()
                        , null,
                        author.selectSingleNode("lastName").getText()
                        , null);
                authorsData.add(a);
            }
        }
        authorsTable.setItems(authorsData);
    }

    public void handleAddAuthor(ActionEvent actionEvent) throws IOException {
        if (isNullOrWhiteSpace(idTF.getText())) {
            generateRandomID();
        }
        if (support.isInvalid()) {
            return;
        }
        if (isNullOrWhiteSpace(mNameTF.getText()) && isNullOrWhiteSpace(nationalityTF.getText())) {
            Author author = new Author(idTF.getText(), fNameTF.getText(), null, lNameTF.getText(), null);
            Element authorElement = xmlRoot.addElement("author")
                    .addAttribute("id", author.getId());
            authorElement.addElement("firstName")
                    .addText(author.getFirstName());
            authorElement.addElement("lastName")
                    .addText(author.getLastName());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            idTF.clear();
            fNameTF.clear();
            lNameTF.clear();
            authorsData.clear();
            buildTableViewData();
        } else if (!isNullOrWhiteSpace(mNameTF.getText()) && isNullOrWhiteSpace(nationalityTF.getText())) {
            Author author = new Author(idTF.getText(), fNameTF.getText(), mNameTF.getText(), lNameTF.getText(), null);
            Element authorElement = xmlRoot.addElement("author")
                    .addAttribute("id", author.getId());
            authorElement.addElement("firstName")
                    .addText(author.getFirstName());
            authorElement.addElement("middleName")
                    .addText(author.getMiddleName());
            authorElement.addElement("lastName")
                    .addText(author.getLastName());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            idTF.clear();
            fNameTF.clear();
            lNameTF.clear();
            mNameTF.clear();
            authorsData.clear();
            buildTableViewData();
        } else {
            Author author = new Author(idTF.getText(), fNameTF.getText(), mNameTF.getText(), lNameTF.getText(), nationalityTF.getText());
            Element authorElement = xmlRoot.addElement("author")
                    .addAttribute("id", author.getId());
            authorElement.addElement("firstName")
                    .addText(author.getFirstName());
            authorElement.addElement("middleName")
                    .addText(author.getMiddleName());
            authorElement.addElement("lastName")
                    .addText(author.getLastName());
            authorElement.addElement("nationality")
                    .addText(author.getNationality());
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            idTF.clear();
            fNameTF.clear();
            lNameTF.clear();
            mNameTF.clear();
            nationalityTF.clear();
            authorsData.clear();
            buildTableViewData();
        }
    }

    public void handleDeleteAuthor(ActionEvent actionEvent) throws IOException {
        if (isNullOrWhiteSpace(idTF.getText())) {
            return;
        }
        String id = idTF.getText();
        String xPathExpression = "/library/author[@id='%s']";
        xPathExpression = String.format(xPathExpression, id);
        Node node = document.selectSingleNode(xPathExpression);
        if (node == null) {
            System.out.println("Author Not Found");
        } else {
            node.detach();
            XMLWriter writer = new XMLWriter(
                    new FileWriter("src/sample/library.xml")
            );
            writer.write(document);
            writer.close();
            System.out.println("Author's been deleted");
        }
        idTF.clear();
        fNameTF.clear();
        lNameTF.clear();
        mNameTF.clear();
        nationalityTF.clear();
        authorsData.clear();
        buildTableViewData();
    }

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }

}

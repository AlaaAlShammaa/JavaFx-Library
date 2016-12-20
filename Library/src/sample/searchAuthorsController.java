package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 * Created by alaa on 11/26/16.
 */
public class searchAuthorsController {
    private ObservableList<Author> authorsData;
    Document document;
    @FXML
    private TableView<Author> authorsTable;
    @FXML
    CheckBox nationalityCB;
    @FXML
    TextField searchField;
    @FXML
    private TableColumn<Author, String> fNameCol;
    @FXML
    private TableColumn<Author, String> mNameCol;
    @FXML
    private TableColumn<Author, String> lNameCol;
    @FXML
    private TableColumn<Author, String> nationalityCol;
    private Element xmlRoot;

    public void initialize() throws DocumentException {
        authorsData = FXCollections.observableArrayList();
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        mNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nationalityCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        nationalityCB.setOnAction(event -> {
            authorsData.clear();
            searchField.clear();
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            authorsData.clear();
        });
    }

    public void handleSearch(ActionEvent actionEvent) throws DocumentException {
        File inOutFile = new File("src/sample/library.xml");
        SAXReader reader = new SAXReader();
        document = reader.read(inOutFile);
        xmlRoot = document.getRootElement();
        if (!searchField.getText().isEmpty()) {
            if (nationalityCB.isSelected()) {
                String nationality = searchField.getText();
                String xPathExpression = "library/author";
                List<Node> authors = document.selectNodes(xPathExpression);
                if (authors.size() == 0) {
                    return;
                }
                for (Node author : authors) {
                    String id = null;
                    String fName = null;
                    String lName = null;
                    String nat = null;
                    String mName = null;
                    if (author.selectSingleNode("nationality") != null)
                        if (author.selectSingleNode("nationality").getText().equals(nationality)) {
                            id = author.valueOf("@id");
                            fName = author.selectSingleNode("firstName").getText();
                            if (author.selectSingleNode("middleName") != null) {
                                mName = author.selectSingleNode("middleName").getText();
                            }
                            lName = author.selectSingleNode("lastName").getText();
                            if (author.selectSingleNode("nationality") != null) {
                                nat = author.selectSingleNode("nationality").getText();
                            }
                        }
                    if (id != null) {
                        Author a = new Author(id, fName, mName, lName, nat);
                        authorsData.add(a);
                    }
                }
            }
        }
        authorsTable.setItems(authorsData);
    }
}

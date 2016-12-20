package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by alaashammaa on 11/10/16.
 */
public class Author {
    SimpleStringProperty id;
    SimpleStringProperty firstName;
    SimpleStringProperty middleName;
    SimpleStringProperty lastName;
    SimpleStringProperty nationality;

    public Author() {
    }

    public Author(String id, String firstName, String middleName, String lastName, String nationality) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName = new SimpleStringProperty(middleName);
        this.lastName = new SimpleStringProperty(lastName);
        this.nationality = new SimpleStringProperty(nationality);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getMiddleName() {
        return middleName.get();
    }

    public SimpleStringProperty middleNameProperty() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName.set(middleName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getNationality() {
        return nationality.get();
    }

    public SimpleStringProperty nationalityProperty() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName=" + firstName +
                ", middleName=" + middleName +
                ", lastName=" + lastName +
                ", nationality=" + nationality +
                '}';
    }
}

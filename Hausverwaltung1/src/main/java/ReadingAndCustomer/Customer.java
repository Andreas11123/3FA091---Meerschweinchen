package ReadingAndCustomer;

import java.time.LocalDate;
import java.util.UUID;

public class Customer implements ICustomer {

    // Attributes
    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private ICustomer.Gender gender;
    private UUID id;

    // Constructor
    public Customer(String firstname, String lastname, LocalDate birthdate, Customer.Gender gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.id = UUID.randomUUID(); // Generate a random unique ID
    }

    // Default constructor
    public Customer() {
        this.id = UUID.randomUUID(); // Generate a random unique ID
    }

    // Getters
    @Override
    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    @Override
    public String getFirstname() {
        return this.firstname;
    }

    @Override
    public ICustomer.Gender getGender() {
        return this.gender;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    // Setters
    @Override
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public void setGender(ICustomer.Gender gender) {
        this.gender = gender;
    }

    @Override
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    // toString method for better object representation
    @Override
    public String toString() {
        return "Customer [ID=" + id + ", First Name=" + firstname + ", Last Name=" + lastname
                + ", Birth Date=" + birthdate + ", Gender=" + gender + "]";
    }
}

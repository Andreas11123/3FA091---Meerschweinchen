package ReadingAndCustomer;

import java.time.LocalDate;
import java.util.UUID;

public class Customer implements ICustomer {

    // Attributes
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private ICustomer.Gender gender;
    private UUID id;

    // Constructor
    public Customer(String firstName, String lastName, LocalDate birthDate, Customer.Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.id = UUID.randomUUID(); // Generate a random unique ID
    }

    // Default constructor
    public Customer() {
        this.id = UUID.randomUUID(); // Generate a random unique ID
    }

    // Getters
    @Override
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public ICustomer.Gender getGender() {
        return this.gender;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    // Setters
    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setGender(ICustomer.Gender gender) {
        this.gender = gender;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    // toString method for better object representation
    @Override
    public String toString() {
        return "Customer [ID=" + id + ", First Name=" + firstName + ", Last Name=" + lastName
                + ", Birth Date=" + birthDate + ", Gender=" + gender + "]";
    }
}

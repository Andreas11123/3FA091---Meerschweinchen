import java.time.LocalDate;
import java.util.UUID;

public class Customer implements ICustomer {
    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public String getFirstName() {
        return "";
    }

    @Override
    public Gender getGender() {
        return null;
    }

    @Override
    public String getLastName() {
        return "";
    }

    @Override
    public void setBirthDate(LocalDate birtDate) {

    }

    @Override
    public void setFirstName(String firstName) {

    }

    @Override
    public void setGender(Gender gender) {

    }

    @Override
    public void setLastName(String lastName) {

    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public void setId(UUID id) {

    }
}

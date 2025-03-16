package ReadingAndCustomer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;

@JsonDeserialize(as = Customer.class)
@JsonSerialize(as = Customer.class)
public interface ICustomer extends IId {

    enum Gender {
        D, // divers
        M, // männlich
        U, // unbekannt
        W; // weiblich
    }


    LocalDate getBirthdate();

    String getFirstname();

    Gender getGender();

    String getLastname();

    void setBirthdate(LocalDate birtDate);

    void setFirstname(String firstname);

    void setGender(Gender gender);

    void setLastname(String lastname);

}

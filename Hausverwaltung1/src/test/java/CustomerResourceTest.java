import DataConnection.Util;
import ReadingAndCustomer.Customer;
import ReadingAndCustomer.ICustomer;
import dev.bsinfo.ressource.CustomerResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerResourceTest {

    private CustomerResource customerResource;
    private UUID testCustomerId;

    @BeforeAll
    void setup() {
        Util.getConnection("Hausverwaltung"); // In-Memory DB
        customerResource = new CustomerResource();
    }

    @Test
    @Order(1)
    void testCreateCustomer() {
        Map<String, Customer> request = new HashMap<>();
        Customer customer = new Customer();
        customer.setFirstname("Test Kunde");
        customer.setBirthdate(LocalDate.of(1990, 5, 20));
        customer.setLastname("Testdatensatz");
        customer.setGender(ICustomer.Gender.D);
        request.put("customer", customer);

        Response response = customerResource.createCustomer(null);
        assertEquals(201, response.getStatus());

        Map<String, Object> responseBody = (Map<String, Object>) response.getEntity();
        assertNotNull(responseBody.get("customer"));
        testCustomerId = ((Customer) responseBody.get("customer")).getId();
        Response badResponse = customerResource.createCustomer(new Customer());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), badResponse.getStatus());
        Map<String, String> badResponseBody = (Map<String, String>) badResponse.getEntity();
        assertEquals("Invalid request format. Expected: {\"customer\": {...}}", badResponseBody.get("error"));
    }

    @Test
    @Order(2)
    void testGetAllCustomers() {
        Response response = customerResource.getAllCustomers();
        assertEquals(200, response.getStatus());
    }

    @Test
    @Order(3)
    void testGetCustomerById() {
        Response response = customerResource.getCustomer(testCustomerId.toString());
        assertEquals(200, response.getStatus());
    }

    @Test
    @Order(4)
    void testUpdateCustomer() {
        Map<String, Customer> request = new HashMap<>();
        Customer customer = new Customer();
        customer.setId(testCustomerId);
        customer.setFirstname("Updated Kunde");
        customer.setBirthdate(LocalDate.of(1990, 5, 20));
        customer.setLastname(" Update Testdatensatz");
        customer.setGender(ICustomer.Gender.D);
        request.put("customer", customer);


        Response response = customerResource.updateCustomer(customer);
        assertEquals(200, response.getStatus());
        assertEquals("Customer updated successfully", response.getEntity());


        Response badResponse = customerResource.updateCustomer(new Customer());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), badResponse.getStatus());
        assertEquals("Invalid request format. Expected: {\"customer\": {...}}", badResponse.getEntity());
        System.out.println(badResponse.getStatus());
        System.out.println(badResponse.getEntity());

        // **FEHLERHAFTER REQUEST** (Kunde ohne ID)
        Customer invalidCustomer = new Customer();
        Map<String, Customer> customerInvalid = new HashMap<>();
        customerInvalid.put("customer", invalidCustomer);

        Response badResponse2 = customerResource.updateCustomer((Customer) customerInvalid);
        System.out.println(badResponse2.getStatus());
        System.out.println(badResponse2.getEntity());
        System.out.println(Response.Status.BAD_REQUEST.getStatusCode());
        System.out.println(badResponse2.getStatus());

//
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), badResponse2.getStatus());
//        assertEquals("Invalid customer data", badResponse2.getEntity());
    }

    @Test
    @Order(5)
    void testDeleteCustomer() {
        Response response = customerResource.deleteCustomer(testCustomerId.toString());
        assertEquals(200, response.getStatus());

        // **FEHLERHAFTER REQUEST** (Ungültige UUID)
        Response badResponse = customerResource.deleteCustomer("invalid-uuid");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), badResponse.getStatus());
        Map<String, String> badResponseBody = (Map<String, String>) badResponse.getEntity();
        assertEquals("Invalid UUID format", badResponseBody.get("error"));
    }

    @Test
    @Order(6)
    void testGetNonExistentCustomer() {
        Response response = customerResource.getCustomer(UUID.randomUUID().toString());
        assertEquals(404, response.getStatus());
    }
}

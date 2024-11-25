/*
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CustomerDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private CustomerDAO customerDAO;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialisiere Mocks und CustomerDAO
        MockitoAnnotations.initMocks(this);
        customerDAO = new CustomerDAO(mockConnection);
    }

    @Test
    public void testAddCustomer() throws SQLException {
        // Arrange
        ICustomer customer = mock(ICustomer.class);
        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getFirstName()).thenReturn("John");
        when(customer.getLastName()).thenReturn("Doe");
        when(customer.getGender()).thenReturn(Gender.MALE);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        customerDAO.addCustomer(customer);

        // Assert
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetCustomerById() throws SQLException {
        // Arrange
        IId customerId = mock(IId.class);
        when(customerId.toString()).thenReturn(UUID.randomUUID().toString());
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        // Act
        ICustomer customer = customerDAO.getCustomerById(customerId);

        // Assert
        assertNotNull(customer);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testGetAllCustomers() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // Simulate one customer found

        // Act
        List<ICustomer> customers = customerDAO.getAllCustomers();

        // Assert
        assertEquals(1, customers.size());
        
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        // Arrange
        ICustomer customer = mock(ICustomer.class);
        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getFirstName()).thenReturn("John");
        when(customer.getLastName()).thenReturn("Doe");
        when(customer.getGender()).thenReturn(Gender.MALE);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        customerDAO.updateCustomer(customer);

        // Assert
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        // Arrange
        IId customerId = mock(IId.class);
        when(customerId.toString()).thenReturn(UUID.randomUUID().toString());
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        customerDAO.deleteCustomer(customerId);

        // Assert
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
*/
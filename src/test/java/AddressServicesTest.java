
import model.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servicesLayer.AddressServices;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class AddressServicesTest {

    AddressServices addressServices = AddressServices.getInstance();


    @Test
    @DisplayName("Test for isAddressFound() method")
    public void isAddressFound(){

        assertTrue(addressServices.isAddressFound("Jordan"));
        assertTrue(addressServices.isAddressFound("Iraq"));
        assertTrue(addressServices.isAddressFound("Saudi"));
        assertFalse(addressServices.isAddressFound("USA"));
        assertFalse(addressServices.isAddressFound("China"));
        assertFalse(addressServices.isAddressFound("Morocco"));

    }



    @Test
    @DisplayName("Test for getAddressByCountryName() method")
    public void getAddressByCountryName(){

        assertNull(addressServices.getAddressByCountryName("Morocco").getCountry());
        assertNull(addressServices.getAddressByCountryName("Morocco").getCity());
        assertNull(addressServices.getAddressByCountryName("Morocco").getPostalCode());

    }


    @Test
    @DisplayName("Test for getAllAddresses() method")
    public void getAllAddresses(){

        assertEquals(6, addressServices.getDataFromAddressList().size());

    }


    @Test
    @DisplayName("Test for createAddress() method")
    public void createAddress() throws IOException {
        Address address = new Address("US");

        assertEquals("US", addressServices.createAddress(address).getCountry());

    }



}

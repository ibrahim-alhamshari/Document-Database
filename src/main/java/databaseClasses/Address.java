package databaseClasses;

public class Address {

    private String street;
    private String city ;
    private String state;
    private String postalCode;

    @Override
    public String toString() {
        return "database.Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}

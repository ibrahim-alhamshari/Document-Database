package model;

public class Address {

    private Long id;
    private String country;
    private String city;
    private String postalCode;

    public Address(){

    }
    public Address(String country){ //Telescope pattern
        this(country , null);
    }

    public Address(String country , String city){
        this(country , city , null);
    }
    public Address(String country, String city, String postalCode) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
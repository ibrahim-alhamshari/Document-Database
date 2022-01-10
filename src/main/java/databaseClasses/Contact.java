package databaseClasses;

public class Contact {

    private String firstName;
    private String lastName;
    private int age;

    public Contact(String firstName, String lastName , int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age=age;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}

package databaseClasses;

public class PhoneNumber {

    private String type;
    private String phone;

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "type='" + type + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

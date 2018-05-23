package be.wget.inpres.java.restaurant.dataobjects;

/**
 *
 * @author wget
 */
public class Waiter {
    protected String lastName;
    protected String firstName;
    protected String login;
    protected String idCardNumber;

    public Waiter(String lastName, String firstName, String login,
                  String idCardNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.idCardNumber = idCardNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLogin() {
        return login;
    }
    
    public String getIdCardNumber() {
        return this.getIdCardNumber();
    }
}

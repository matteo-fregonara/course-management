package nl.tudelft.sem.template.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class User {

    @Id
    @Column(nullable = false)
    private String netID;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;

    protected User() {
    }

    /**
     * Constructor for User entity.
     *
     * @param netID netId of the user
     * @param password password of the user
     * @param roles roles of the user
     */
    public User(String netID, String password, String roles) {
        this.netID = netID;
        this.password = password;
        this.roles = roles;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setNetID(String netID) {
        this.netID = netID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNetID() {
        return netID;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(netID, user.netID)
                && Objects.equals(password, user.password)
                && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(netID, password);
    }
}

package nl.tudelft.sem.template.entities;

public class UserInfo {
    public String netID;
    public String roles;

    /**
     * Default constructor.
     */
    public UserInfo() {

    }

    /**
     * Initiates the UserInfo class with netID and roles.
     *
     * @param netID netID of the user.
     * @param roles roles of the user.
     */
    public UserInfo(String netID, String roles) {
        this.netID = netID;

        this.roles = roles;
    }

    public String getNetID() {
        return netID;
    }

    public void setNetID(String netID) {
        this.netID = netID;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}

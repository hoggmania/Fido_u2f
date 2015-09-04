package fr.neowave.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Représentation d'un utilisateur de la table Users
 */
public class User implements Serializable{

    private String username;
    private String password;
    private Boolean suspended;
    private List<Registration> registrations = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }


}

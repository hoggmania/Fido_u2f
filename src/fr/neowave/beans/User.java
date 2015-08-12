package fr.neowave.beans;

/**
 * Created by Elekhyr on 04/08/2015.
 */
public class User {

    private String username;
    private String password;
    private Boolean suspended;

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

    public String toJSON(){
        return "{ username :".concat(username).concat(", password:").concat(password).concat(", suspended:").concat(String.valueOf(suspended).concat("}"));
    }
}

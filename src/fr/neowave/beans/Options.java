package fr.neowave.beans;

/**
 * Created by root on 07/08/15.
 */
public class Options {

    private Boolean neowaveAccepted;
    private Boolean adminReplaceUsersTokens;
    private Boolean userCreateAccount;
    private Boolean homeProtected;
    private Boolean usersRegisterTheirOwnFirstToken;
    private Boolean usersAddNewTokens;
    private Boolean usersRemoveLastToken;
    private Boolean usersSeeDetails;

    public Boolean getNeowaveAccepted() {
        return neowaveAccepted;
    }

    public void setNeowaveAccepted(Boolean neowaveAccepted) {
        this.neowaveAccepted = neowaveAccepted;
    }

    public Boolean getAdminReplaceUsersTokens() {
        return adminReplaceUsersTokens;
    }

    public void setAdminReplaceUsersTokens(Boolean adminReplaceUsersTokens) {
        this.adminReplaceUsersTokens = adminReplaceUsersTokens;
    }

    public Boolean getUserCreateAccount() {
        return userCreateAccount;
    }

    public void setUserCreateAccount(Boolean userCreateAccount) {
        this.userCreateAccount = userCreateAccount;
    }

    public Boolean getHomeProtected() {
        return homeProtected;
    }

    public void setHomeProtected(Boolean homeProtected) {
        this.homeProtected = homeProtected;
    }

    public Boolean getUsersRegisterTheirOwnFirstToken() {
        return usersRegisterTheirOwnFirstToken;
    }

    public void setUsersRegisterTheirOwnFirstToken(Boolean usersRegisterTheirOwnFirstToken) {
        this.usersRegisterTheirOwnFirstToken = usersRegisterTheirOwnFirstToken;
    }

    public Boolean getUsersAddNewTokens() {
        return usersAddNewTokens;
    }

    public void setUsersAddNewTokens(Boolean usersAddNewTokens) {
        this.usersAddNewTokens = usersAddNewTokens;
    }

    public Boolean getUsersRemoveLastToken() {
        return usersRemoveLastToken;
    }

    public void setUsersRemoveLastToken(Boolean usersRemoveLastToken) {
        this.usersRemoveLastToken = usersRemoveLastToken;
    }

    public Boolean getUsersSeeDetails() {
        return usersSeeDetails;
    }

    public void setUsersSeeDetails(Boolean usersSeeDetails) {
        this.usersSeeDetails = usersSeeDetails;
    }
}

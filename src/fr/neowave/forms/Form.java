package fr.neowave.forms;

import fr.neowave.beans.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formulaire de renvoie, classe mère de tous les formulaires
 */
public class Form {

    private String message;
    private List<User> users = new ArrayList<>();
    private Object object = new Object();
    private Map<String, String> errors = new HashMap<>();

    public Form() {
        errors = new HashMap<>();
    }

    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    protected void setError(String errorName, String error) {
        this.errors.put(errorName, error);
    }
}

package fr.neowave.forms;

import java.util.HashMap;
import java.util.Map;

public class FormResponse {

    private String message = null;
    private Map<String, String> errors = new HashMap<>();

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setError( String errorName, String message ) {
        errors.put( errorName, message );
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJSON(){
        return "{ message :".concat(message).concat("}");
    }
}

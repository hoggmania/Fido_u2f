package fr.neowave.forms.Exceptions;

public enum FormErrors {

    USERNAME_ERR ("username"),
    PASSWORD_ERR ("password"),
    DEFAULT_ERR ("default");


    private String name = "";

    FormErrors(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}

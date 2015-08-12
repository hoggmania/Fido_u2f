package fr.neowave.servlets;

public enum ServletEnum {
    PATH ("/WEB-INF"),

    JSP_AUTHENTICATION("/authentication.jsp"),
    JSP_INDEX ("/index.jsp"),
    JSP_KEYLIST ("/keyList.jsp"),
    JSP_OPTIONS ("/options.jsp"),
    JSP_PROTECTEDPAGE ("/protectedPage.jsp"),
    JSP_REGISTRATION ("/registration.jsp"),
    JSP_USERSMANAGER ("/usersManager.jsp"),

    USER ("user"),
    PASSWORD ("password"),
    FORM ("form");

    private String name = "";

    ServletEnum(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}

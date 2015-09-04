package fr.neowave.messages;

public class Messages {

    /*
     * U2F ERRORS CODE
     */
    public static final String OTHER_ERROR                          = "Other error";
    public static final String BAD_REQUEST                          = "Bad request";
    public static final String CONFIGURATION_UNSUPPORTED            = "Configuration unsupported";
    public static final String DEVICE_INELIGIBLE                    = "Bad token";
    public static final String TIMEOUT                              = "Timeout";
    public static final String UNKNOWN_ERROR                        = "Unknown device error";



    public static final String ALREADY_AUTHENTICATED                = "You are already authenticated";
    public static final String U2F_TOKEN_REGISTRATION_NEEDED        = "You need to register an u2f token to access this page";
    public static final String U2F_TOKEN_AUTHENTICATED_NEEDED       = "You need to authenticate an u2f token to access this page";
    public static final String ALREADY_REGISTERED                   = "You are already registered";
    public static final String AUTHENTICATION_NEEDED                = "You have to sign in first";

    public static final String ADMIN_NO_TOKEN_REGISTERED            = "You have to register your first token";
    public static final String ADMIN_NO_TOKEN_AUTHENTICATED         = "You have to authenticate your token";

    public static final String PASSWORD_PROTECTED_PAGE              = "You are on the protected by password page";
    public static final String U2F_PROTECTED_PAGE                   = "You are on the protected by u2f page";

    public static final String PUT_TOKEN                            = "Please put your token";

    public static final String ERROR_404                            = "The page you asked for doesn't exist";


    /*
     * SERVER SETUP
     */

    public static final String ONLY_NEOWAVE                         = "1) Only Neowave token ?";
    public static final String ADMIN_REPLACE_USER_TOKEN             = "2) Can admin replace user token ?";
    public static final String USER_CREATE_ACCOUNT                  = "3) Can user create account ?";
    public static final String USER_REGISTER_THEIR_OWN_FIRST_TOKEN  = "4) Can user register their own first token ?";
    public static final String USER_ADD_NEW_TOKEN                   = "5) Can user add new token ?";
    public static final String USER_REMOVE_LAST_TOKEN               = "6) Can user remove their last token ?";
    public static final String USER_SEE_DETAILS                     = "7) Can user see details ?";

    public static final String YES                                  = "a) yes";
    public static final String NO                                   = "b) no";



    /*
     * Authentication errors
     */
    public static final String AUTH_USERNAME_WRONG_CHARACTERS       = "Username not found";
    public static final String AUTH_USERNAME_WRONG_LENGTH           = "Username not found";
    public static final String AUTH_PASSWORD_WRONG_CHARACTERS       = "Wrong password";
    public static final String AUTH_PASSWORD_WRONG_LENGTH           = "Wrong password";
    public static final String AUTH_USERNAME_NOT_FOUND              = "Username not found";
    public static final String AUTH_WRONG_PASSWORD                  = "Wrong password";
    public static final String AUTH_SUSPENDED_ACCOUNT               = "Account suspended by admin";


    /*
     * Admin manage errors
     */
    public static final String MANAGE_CANT_DELETE_YOURSELF = "You cannot delete yourself";
    public static final String MANAGE_CANT_SUSPEND_YOURSELF = "You cannot suspend yourself";
    public static final String MANAGE_CANT_DELETE_LAST_TOKEN = "You cannot delete your last token";
    public static final String MANAGE_CANT_SUSPEND_LAST_TOKEN = "You cannot suspend your last token";
    public static final String MANAGE_USERNAME_WRONG_CHARACTERS = "Username not found";
    public static final String MANAGE_USERNAME_WRONG_LENGTH = "Username not found";
    public static final String MANAGE_USER_NOT_FOUND = "User not found";
    public static final String MANAGE_PASSWORD_WRONG_CHARACTERS = "Wrong password";

    /*
     * Options
     */
    public static final String OPTIONS_SUCCESS = "Options have been changed";
    public static final String OPTIONS_ERROR = "Bad value";

    /*
     * Registration errors
     */
    public static final String REG_CANT_CREATE_ACCOUNT = "Option 3) b) Can't create account ";
    public static final String REG_USERNAME_WRONG_CHARACTERS = "Allowed characters are a-z A-Z 0-9 . _ @.";
    public static final String REG_USERNAME_WRONG_LENGTH = "Username size must be between 6 and 32.";
    public static final String REG_USERNAME_ALREADY_EXISTS = "User already exists.";
    public static final String REG_PASSWORD_WRONG_CHARACTERS = "Allowed characters are a-z A-Z 0-9.";
    public static final String REG_PASSWORD_WRONG_SIZE = "Password size must be between 6 and 32.";
    public static final String REG_PASSWORDS_DONT_MATCH = "Passwords don't match.";

    /*
     * U2F authentication
     */
    public static final String U2F_AUTH_NO_TOKEN_REGISTERED = "You don't have any registered token";
    public static final String U2F_AUTH_TOKEN_ALL_SUSPENDED = "Your tokens are all suspended";
    public static final String U2F_AUTH_REQUEST_TIMEOUT = "Request timeout please try again";

    /*
     * U2F registration
     */
    public static final String U2F_REG_CANT_ADD_TOKEN = "You can't add your first token";
    public static final String U2F_REG_CANT_ADD_NEW_TOKEN = "You can't add a new token";
    public static final String U2F_REG_REQUEST_TIMEOUT = "Request timeout";
    public static final String U2F_REG_ONLY_NEOWAVE = "You can register only neowave token";

    /*
     * Token manage
     */
    public static final String TOKEN_CANT_DELETE_LAST_TOKEN = "You can't remove your last token, option 7) a) activated";
    public static final String USER_TOKEN_DELETED           = "Token has been deleted";

}

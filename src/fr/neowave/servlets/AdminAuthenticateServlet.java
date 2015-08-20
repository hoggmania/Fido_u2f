package fr.neowave.servlets;

import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.AuthenticationForm;
import fr.neowave.forms.U2fAuthenticationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/adminAuthentication")
public class AdminAuthenticateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

    }


    /*
    * Si mon nom d'utilisateur est pas admin je retourne une erreur
    * Sinon j'effectue la connexion grace à la méthode doAuthentication de la classe AuthentificationForm
    *   S'il y a des erreurs je les renvoie
    *   Sinon j'indique s'il a une clé enregistrée ou pas
    *       S'il  a une clé enregistré on stocke dans la session la requete d'authentification de la clé ainsi que le nom d'utilisateur temporaire en horodatant la requete
    *         puis on procede à l'authentification parclé
    *       Sinon on stocke le nom d'utilisateur temporaire en l'horodatant puis on procède à l'enregistrement par clé  */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(!request.getParameter("username").equals("admin")){

            request.setAttribute("errors", new HashMap<String, String>().put("username","Bad username"));
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

        }else{

            AuthenticationForm authenticationForm = new AuthenticationForm();
            authenticationForm.startAuthentication(request);

            if(authenticationForm.getFormResponse().getErrors().isEmpty()){
                try {
                    request.getSession().setAttribute("hasKey", !DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list("admin").isEmpty());

                    U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();
                    if((Boolean) request.getSession().getAttribute("hasKey")){
                        u2fAuthenticationForm.startU2fAuthentication(request, DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list("admin"));
                        String authenticationChallenge = Base64.getEncoder().encodeToString(u2fAuthenticationForm.getFormResponse().getMessage().getBytes());
                        request.getSession().setAttribute("tempUsername", "admin");
                        request.getSession().setAttribute("authenticationChallenge", authenticationChallenge);
                        request.getSession().setAttribute("authenticationTimestamp", System.currentTimeMillis());
                        this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request,response);

                    } else{
                        request.getSession().setAttribute("username", "admin");
                        response.sendRedirect(request.getContextPath().concat("/adminU2fRegister?from=").concat(request.getRequestURL().toString()));

                    }
                } catch (SQLException | ClassNotFoundException | ParseException e) {

                    authenticationForm.getFormResponse().setError("default", e.getMessage());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

                }
            }
            else{
                request.setAttribute("errors", authenticationForm.getFormResponse().getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

            }
        }


    }//end doPost

}//end AdminAuthenticateServlet

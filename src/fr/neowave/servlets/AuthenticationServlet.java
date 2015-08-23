package fr.neowave.servlets;

import fr.neowave.beans.Logger;
import fr.neowave.beans.User;
import fr.neowave.forms.AuthenticationForm;
import fr.neowave.forms.U2fAuthenticationForm;
import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;


@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

    private Logger logger;

    public AuthenticationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null){

            this.getServletContext().getRequestDispatcher("/WEB-INF/user/passwordAuthentication.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{


            AuthenticationForm authenticationForm = new AuthenticationForm();
            authenticationForm.startAuthentication(request);

            if(authenticationForm.getErrors().isEmpty()){

                response.sendRedirect(request.getContextPath().concat("/index"));
            }
            else{
                request.setAttribute("errors", authenticationForm.getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/user/passwordAuthentication.jsp").forward(request,response);

            }
        }

}

package fr.neowave.servlets;

import fr.neowave.forms.AuthenticationForm;
import fr.neowave.forms.U2fAuthenticationForm;
import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/adminAuthentication")
public class AdminAuthenticateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getParameter("username") == null || !request.getParameter("username").equals("admin")){
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

        }else{

            AuthenticationForm authenticationForm = new AuthenticationForm();
            authenticationForm.startAuthentication(request);

            if(authenticationForm.getErrors().isEmpty()){

                    U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();
                    if((Boolean) request.getSession().getAttribute("hasKey")){
                        u2fAuthenticationForm.startU2fAuthentication(request);
                        response.sendRedirect(request.getContextPath().concat("/adminU2fAuthenticate"));
                    } else{
                        U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                        u2fRegistrationForm.startU2fRegistration(request);
                        response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));

                    }
            }
            else{
                request.setAttribute("errors", authenticationForm.getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

            }
        }


    }//end doPost

}//end AdminAuthenticateServlet

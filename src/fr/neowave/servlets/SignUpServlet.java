package fr.neowave.servlets;

import fr.neowave.beans.User;
import fr.neowave.forms.RegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {

    public SignUpServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if( request.getSession().getAttribute("username") == "admin" || request.getSession().getAttribute("username") == null ){
            this.getServletContext().getRequestDispatcher("/WEB-INF/signUp.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        response.setContentType("application/json");

        RegistrationForm registrationForm = new RegistrationForm();
        User user = registrationForm.register(request);

        HttpSession session = request.getSession();

        if(registrationForm.getFormResponse().getErrors().isEmpty()){

            if(request.getSession().getAttribute("username") == "admin"){
                session.setAttribute("actionPerformed", user.getUsername().concat("'s account has been created."));
                response.sendRedirect(request.getContextPath().concat("/index"));
            }
            else{
                session.setAttribute("username", user.getUsername());
                session.setMaxInactiveInterval(86400000);
                response.sendRedirect(request.getContextPath().concat("/index"));
            }

        }
        else{
            session.setAttribute("username", null);
            request.setAttribute("username", request.getParameter("username"));
            request.setAttribute("form", registrationForm.getFormResponse());
            this.getServletContext().getRequestDispatcher("/WEB-INF/signUp.jsp").forward(request, response);
        }
    }
}

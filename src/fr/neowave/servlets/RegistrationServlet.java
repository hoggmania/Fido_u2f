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


@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    public RegistrationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/WEB-INF/registration.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        response.setContentType("application/json");

        RegistrationForm registrationForm = new RegistrationForm();
        User user = registrationForm.register(request);

        HttpSession session = request.getSession();

        if(registrationForm.getFormResponse().getErrors() == null || registrationForm.getFormResponse().getErrors().isEmpty()){


            session.setAttribute(ServletEnum.USER.toString(), user.getUsername());

            this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        }
        else{
            session.setAttribute(ServletEnum.USER.toString(), null);
            request.setAttribute(ServletEnum.USER.toString(), user);
            request.setAttribute(ServletEnum.FORM.toString(), registrationForm.getFormResponse());
            this.getServletContext().getRequestDispatcher("/WEB-INF/registration.jsp").forward(request, response);
        }
    }
}

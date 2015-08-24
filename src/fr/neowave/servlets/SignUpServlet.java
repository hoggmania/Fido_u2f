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
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin") ){
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/signUp.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

        RegistrationForm registrationForm = new RegistrationForm();
        User user = registrationForm.register(request);

        HttpSession session = request.getSession();

        if(registrationForm.getErrors().isEmpty()){

            if(request.getSession().getAttribute("username") != null && !request.getSession().getAttribute("username").equals("admin")){
                response.sendRedirect(request.getContextPath().concat("/index"));
            }
            else{
                session.setAttribute("username", user.getUsername());
                request.getSession().setAttribute("hasKey", false);
                request.getSession().setAttribute("u2fAuthenticated", false);
                session.setMaxInactiveInterval(86400000);
                response.sendRedirect(request.getContextPath().concat("/index"));
            }

        }
        else{
            session.setAttribute("username", null);
            request.setAttribute("username", request.getParameter("username"));

            request.setAttribute("errors", registrationForm.getErrors());
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/signUp.jsp").forward(request, response);
        }
    }
}

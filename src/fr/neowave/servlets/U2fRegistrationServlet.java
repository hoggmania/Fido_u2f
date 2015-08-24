package fr.neowave.servlets;

import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;


@WebServlet("/u2fRegister")
public class U2fRegistrationServlet extends HttpServlet {

    public U2fRegistrationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
        else{
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fRegistration.jsp").forward(request,response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
        else {
            HttpSession session = request.getSession();

            if (session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null) {
                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if (u2fRegistrationForm.getErrors().isEmpty()) {
                    response.sendRedirect(request.getContextPath().concat("/"));
                } else {
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fRegistration.jsp").forward(request, response);
                }
                if (session.getAttribute("registrationChallenge") != null)
                    session.removeAttribute("registrationChallenge");
            } else {

                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.startU2fRegistration(request);
                if (!u2fRegistrationForm.getErrors().isEmpty()) {
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                }

                this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fRegistration.jsp").forward(request, response);
            }
        }
    }// end doPost

}// end class

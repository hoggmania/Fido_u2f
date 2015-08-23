package fr.neowave.servlets;

import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/adminU2fRegister")
public class AdminU2FRegisterServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") != null && request.getSession().getAttribute("username").equals("admin")){
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
        }else {
            response.sendRedirect(request.getContextPath().concat("/404"));
        }

    }

    /*
    Si l'utilisateur n'est pas un admin => 404 error
    Sinon s'il est admin
        Si le challenge a été envoyé à la clé et qu'elle a répondu, on procède à l'enregistrement de celle ci
            Si l'enregistrement s'est bien passsé on se redirige vers
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        } else{

            if(session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null){
                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if(u2fRegistrationForm.getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/adminManage"));
                }
                else{
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
                }
            }else{

                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.startU2fRegistration(request);
                if(!u2fRegistrationForm.getErrors().isEmpty()){
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                }

                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
            }
        }
    }
}

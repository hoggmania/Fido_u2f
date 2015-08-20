package fr.neowave.servlets;

import fr.neowave.forms.U2fRegistrationForm;
import u2f.U2F;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/adminU2fRegister")
public class AdminU2FRegisterServlet extends HttpServlet {


    /*
    Si l'utilisateur n'est pas un admin => 404 error
    Sinon s'il est admin
        Si le challenge a été envoyé à la clé et qu'elle a répondu, on procède à l'enregistrement de celle ci
            Si l'enregistrement s'est bien passsé on se redirige vers
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(!request.getSession().getAttribute("username").equals("admin") && request.getParameter("username") == null){
            response.sendRedirect(request.getContextPath().concat("/404"));
        } else{

            if(session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null){
                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doRegistration(request);
                if(u2fRegistrationForm.getFormResponse().getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/admin/manager"));
                }
                else{
                    request.setAttribute("errors", u2fRegistrationForm.getFormResponse().getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
                }
                if(session.getAttribute("registrationChallenge") != null) session.removeAttribute("registrationChallenge");
            }else{

                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.regChallenge(request);
                if(u2fRegistrationForm.getFormResponse().getErrors().isEmpty()){
                    session.setAttribute("registrationChallenge", u2fRegistrationForm.getFormResponse().getMessage());

                }
                else{
                    request.setAttribute("errors",u2fRegistrationForm.getFormResponse().getErrors());
                }
            }
        }
    }
}

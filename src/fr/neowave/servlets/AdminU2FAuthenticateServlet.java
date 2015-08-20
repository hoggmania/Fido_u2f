package fr.neowave.servlets;

import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.U2fAuthenticationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/adminU2fAuthenticate")
public class AdminU2FAuthenticateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(!request.getSession().getAttribute("tempUsername").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        }
        else{
            U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();
            u2fAuthenticationForm.doAuthentication(request);
            if(u2fAuthenticationForm.getFormResponse().getErrors().isEmpty()){
                request.getSession().setAttribute("username", request.getSession().getAttribute("tempUsername"));
                request.getSession().removeAttribute("tempUsername");
                request.getSession().removeAttribute("authenticateChallenge");

                response.sendRedirect(request.getContextPath().concat("/admin/manage"));
            }
            else{
                request.setAttribute("errors", u2fAuthenticationForm.getFormResponse().getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request, response);
            }
        }
    }
}

package fr.neowave.servlets;

import fr.neowave.forms.U2fAuthenticationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/adminU2fAuthenticate")
public class AdminU2FAuthenticateServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("tempAdmin") != null || !request.getSession().getAttribute("tempAdmin").equals("admin")){
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request,response);
        }else{
            response.sendRedirect(request.getContextPath().concat("/404"));
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("tempAdmin") == null || !request.getSession().getAttribute("tempAdmin").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        }
        else{
            U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();
            u2fAuthenticationForm.doU2fAuthentication(request);
            if(u2fAuthenticationForm.getErrors().isEmpty()){

                response.sendRedirect(request.getContextPath().concat("/adminManage"));
            }
            else{
                request.setAttribute("errors", u2fAuthenticationForm.getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request, response);
            }
        }
    }
}

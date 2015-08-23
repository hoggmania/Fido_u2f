package fr.neowave.servlets;


import fr.neowave.forms.UsersTokenForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/manage")
public class ManageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
        else{
            UsersTokenForm usersTokenForm = new UsersTokenForm();
           usersTokenForm.showToken(request);
            if(usersTokenForm.getErrors().isEmpty()){
                request.setAttribute("registrations", usersTokenForm.getObject());
            }else{
                request.setAttribute("errors", usersTokenForm.getErrors());
            }

            this.getServletContext().getRequestDispatcher("/WEB-INF/user/manage.jsp").forward(request,response);

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
        else{
            UsersTokenForm usersTokenForm = new UsersTokenForm();
            usersTokenForm.deleteToken(request);
            if (usersTokenForm.getErrors().isEmpty()){
                request.setAttribute("success", usersTokenForm.getMessage());
                usersTokenForm.showToken(request);
                if(usersTokenForm.getErrors().isEmpty()){
                    request.setAttribute("registrations", usersTokenForm.getObject());
                }else{
                    request.setAttribute("errors", usersTokenForm.getErrors());
                }
            }
            else{
                request.setAttribute("errors", usersTokenForm.getErrors());
            }
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/manage.jsp").forward(request,response);
        }
    }
}

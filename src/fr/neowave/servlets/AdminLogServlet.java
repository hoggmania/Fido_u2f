package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/log")
public class AdminLogServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        }
        else{
            if(!Boolean.valueOf(String.valueOf(request.getSession().getAttribute("hasKey")))) {
                response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
            } else{
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/log.jsp").forward(request,response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        } else{
            if(!Boolean.valueOf(String.valueOf(request.getSession().getAttribute("hasKey")))) {
                response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
            } else{
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/log.jsp").forward(request,response);
            }
        }
    }
}

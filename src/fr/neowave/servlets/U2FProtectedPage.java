package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet("/u2fProtectedPage")
public class U2FProtectedPage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") != null && (Boolean)request.getSession().getAttribute("u2fAuthenticated")){
            if(request.getSession().getAttribute("username").equals("admin")){
                response.sendRedirect(request.getContextPath().concat("/index"));
            }
            else{
                this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fProtectedPage.jsp").forward(request, response);
            }

        }else{
            response.sendRedirect(request.getContextPath().concat("/u2fRegister"));
        }
    }

}

package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/passwordProtectedPage")
public class PasswordProtectedPage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") != null){
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/passwordProtectedPage.jsp");
        }
        else {
            response.sendRedirect(request.getContextPath().concat("/authentication"));
        }
    }

}

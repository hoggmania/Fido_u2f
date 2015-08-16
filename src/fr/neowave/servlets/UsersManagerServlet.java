package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/UsersManager")
public class UsersManagerServlet extends HttpServlet {

    public UsersManagerServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username").equals("admin")) {
            this.getServletContext().getRequestDispatcher("/WEB-INF/options.jsp").forward(request, response);
            if(request.getSession().getAttribute("actionPerformed") != null) request.getSession().removeAttribute("actionPerformed");
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

    }
}

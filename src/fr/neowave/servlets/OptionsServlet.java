package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/options")
public class OptionsServlet extends HttpServlet {

    public OptionsServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == "admin"){
            this.getServletContext().getRequestDispatcher("/WEB-INF/options.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

    }
}

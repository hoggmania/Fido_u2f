package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Elekhyr on 03/08/2015.
 */

@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

    public AuthenticationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{



        this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
    }
}

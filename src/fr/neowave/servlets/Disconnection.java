package fr.neowave.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/disconnection")
public class Disconnection extends HttpServlet {

    public Disconnection(){
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
       try{

           if(request.getSession().getAttribute("username") != null)request.getSession().invalidate();

           response.sendRedirect(request.getContextPath().concat("/authentication"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

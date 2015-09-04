package fr.neowave.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/disconnection")
public class DisconnectionServlet extends HttpServlet {

    public DisconnectionServlet(){
        super();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.addHeader("X-XSS-Protection", "1; mode=block");
        response.addHeader("X-Frame-Options", "DENY; SAMEORIGIN");
        response.addHeader("X-Content-Type-Options", "nosniff");
        response.addHeader("Content-Security-Policy", "img-src 'self';" +
                "media-src 'self';font-src 'self'");
        URLConnection connection = new URL(request.getRequestURL().toString()).openConnection();
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");

        if (cookies != null)
            for(String cookie : cookies){
                response.setHeader("Set-Cookie", cookie.concat("; HttpOnly;"));
            }


        Boolean admin = request.getSession().getAttribute("username") != null && request.getSession().getAttribute("username").equals("admin");
       if(request.getSession().getAttribute("username") != null)request.getSession().invalidate();

       if (admin){

           response.sendRedirect(request.getContextPath().concat("/adminAuthentication"));
       }else{

           response.sendRedirect(request.getContextPath().concat("/authentication"));
       }


    }
}

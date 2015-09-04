package fr.neowave.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/badConfig")
public class BadConfigServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        String userAgent = request.getHeader("User-Agent");
        if(userAgent != null && (!userAgent.contains("Chrome")
                || Integer.valueOf((userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-").replace("Chrome-", "").substring(0,2)) < 42)){
            this.getServletContext().getRequestDispatcher("/WEB-INF/default/BadUserConfig.jsp").forward(request, response);
        }
        else {
            response.sendRedirect(request.getContextPath().concat("/"));
        }
    }
}

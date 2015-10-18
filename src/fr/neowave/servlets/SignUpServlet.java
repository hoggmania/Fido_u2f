package fr.neowave.servlets;

import fr.neowave.forms.RegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


@WebServlet("/signUp")
public class SignUpServlet extends HttpServlet {

    public SignUpServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
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


        this.getServletContext().getRequestDispatcher("/WEB-INF/user/signUp.jsp").forward(request, response);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
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
        if(request.getSession().getAttribute("username") == null || request.getSession().getAttribute("username").equals("admin")) {

            RegistrationForm registrationForm = new RegistrationForm();
            registrationForm.register(request);

            HttpSession session = request.getSession();

            if (registrationForm.getErrors().isEmpty()) {
                if(session.getAttribute("username").equals("admin")){

                    response.sendRedirect(request.getContextPath().concat("/adminManage"));
                }
                else{

                    response.sendRedirect(request.getContextPath().concat("/index"));
                }


            } else {
                if(session.getAttribute("username") != null && !session.getAttribute("username").equals("admin")) session.setAttribute("username", null);
                request.setAttribute("username", request.getParameter("username"));
                request.setAttribute("errors", registrationForm.getErrors());

                this.getServletContext().getRequestDispatcher("/WEB-INF/user/signUp.jsp").forward(request, response);
            }

        }
        else{
            response.sendRedirect(request.getContextPath().concat("/signUp"));
        }

    }
}

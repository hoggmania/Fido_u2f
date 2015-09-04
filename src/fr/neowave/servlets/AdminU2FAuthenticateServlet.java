package fr.neowave.servlets;

import fr.neowave.forms.U2fAuthenticationForm;
import fr.neowave.forms.U2fRegistrationForm;
import fr.neowave.messages.Messages;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/adminU2fAuthenticate")
public class AdminU2FAuthenticateServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        if(request.getSession().getAttribute("tempAdmin") == null || !request.getSession().getAttribute("tempAdmin").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            u2fRegistrationForm.startU2fRegistration(request);
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }else{
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request,response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        if(request.getSession().getAttribute("tempAdmin") == null || !request.getSession().getAttribute("tempAdmin").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            u2fRegistrationForm.startU2fRegistration(request);
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{

            U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();
            if(request.getParameter("authenticate") == null && request.getSession().getAttribute("authenticationChallenge") == null || request.getParameter("response") == null){
                u2fAuthenticationForm.startU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){

                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request, response);
                }
                else{
                    request.setAttribute("errors", u2fAuthenticationForm.getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fAuthentication.jsp").forward(request, response);
                }
            }
            else{
                u2fAuthenticationForm.doU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){

                    response.sendRedirect(request.getContextPath().concat("/adminManage"));
                }
                else{
                    request.setAttribute("errors", u2fAuthenticationForm.getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request, response);
                }
            }

        }
    }
}

package fr.neowave.servlets;


import fr.neowave.forms.UsersTokenForm;
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

@WebServlet("/manage")
public class ManageServlet extends HttpServlet {

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

        if(request.getSession().getAttribute("username") == null ){
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if(request.getSession().getAttribute("hasKey").equals(false)){

            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/u2fRegister?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("u2fAuthenticated").equals(false)) {
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_AUTHENTICATED_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else{
            UsersTokenForm usersTokenForm = new UsersTokenForm();
            usersTokenForm.showToken(request);
            if(usersTokenForm.getErrors().isEmpty()){
                request.setAttribute("registrations", usersTokenForm.getObject());
            }else{
                request.setAttribute("errors", usersTokenForm.getErrors());
            }

            this.getServletContext().getRequestDispatcher("/WEB-INF/user/manage.jsp").forward(request,response);
        }
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
        if(request.getSession().getAttribute("username") == null ){
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if(request.getSession().getAttribute("hasKey").equals(false)){
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/u2fRegister?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("u2fAuthenticated").equals(false)) {
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_AUTHENTICATED_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else{

            UsersTokenForm usersTokenForm = new UsersTokenForm();

            if(request.getParameter("deleteToken") != null && request.getParameter("deleteToken").equals("DT")){
                usersTokenForm.deleteToken(request);
            }


            if (usersTokenForm.getErrors().isEmpty()){
                request.setAttribute("success", true);

            } else {
                request.setAttribute("errors", usersTokenForm.getErrors());
            }
            usersTokenForm.showToken(request);
            request.setAttribute("registrations", usersTokenForm.getObject());
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/manage.jsp").forward(request,response);
        }
    }
}

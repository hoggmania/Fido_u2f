package fr.neowave.servlets;

import fr.neowave.forms.U2fAuthenticationForm;
import fr.neowave.messages.Messages;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/ajaxU2FAuthentication")
public class AjaxU2FAuthenticationServlet extends HttpServlet{



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
        if(request.getSession().getAttribute("username") == null) {
            request.setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }else if(request.getSession().getAttribute("username").equals("admin")){

            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if (request.getSession().getAttribute("hasKey").equals(false)){
            response.sendRedirect(request.getContextPath().concat("/u2fRegister"));

        }
        else{

            this.getServletContext().getRequestDispatcher("/WEB-INF/user/ajaxU2fAuthentication.jsp").forward(request, response);
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        Writer out = response.getWriter();
        StringBuilder stringBuilder = new StringBuilder();

        if(request.getSession().getAttribute("username") == null) {
            request.setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
            return;
        }else if(request.getSession().getAttribute("username").equals("admin")){

            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if (request.getSession().getAttribute("hasKey").equals(false)){
            stringBuilder.append("{ \"success\" : false, \"message\" :");
            stringBuilder.append(Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            stringBuilder.append("}");

        }
        else{
            U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();

            if (request.getSession().getAttribute("authenticationChallenge") != null && request.getParameter("response") != null){
                u2fAuthenticationForm.doU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){
                    stringBuilder.append("{ \"success\" : true, \"message\" :");
                    stringBuilder.append(u2fAuthenticationForm.getMessage());
                    stringBuilder.append("}");
                }
                else{
                    stringBuilder.append("{ \"success\" : false, \"message\" :");
                    stringBuilder.append(u2fAuthenticationForm.getErrors());
                    stringBuilder.append("}");
                }
            }
            else {
                u2fAuthenticationForm.startU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){
                    stringBuilder.append("{ \"success\" : true, \"message\" :");
                    stringBuilder.append(request.getSession().getAttribute("authenticationChallenge"));
                    stringBuilder.append("}");
                }
                else{
                    stringBuilder.append("{ \"success\" : false, \"message\" :\"");
                    stringBuilder.append(u2fAuthenticationForm.getErrors());
                    stringBuilder.append("\"}");
                }
            }
        }
        out.write(stringBuilder.toString());
    }
}

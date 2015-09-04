package fr.neowave.servlets;

import fr.neowave.forms.U2fRegistrationForm;
import fr.neowave.messages.Messages;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/ajaxU2FRegistration")
public class AjaxU2FRegistrationServlet extends HttpServlet{

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
        else if((Boolean)request.getSession().getAttribute("hasKey") && !(Boolean) request.getSession().getAttribute("u2fAuthenticated")){
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate"));
        }
        else {
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/ajaxU2fRegistration.jsp").forward(request, response);
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

        if (request.getSession().getAttribute("username") == null) {
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        } else if (request.getSession().getAttribute("username").equals("admin")) {
            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }else if((Boolean)request.getSession().getAttribute("hasKey") && !(Boolean) request.getSession().getAttribute("u2fAuthenticated")){
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate"));
        } else {
            HttpSession session = request.getSession();

            if (session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null) {


                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if (u2fRegistrationForm.getErrors().isEmpty()) {
                    stringBuilder.append("{\"success\" : true, \"message\" :");
                    stringBuilder.append(u2fRegistrationForm.getMessage());
                    stringBuilder.append("}");
                } else {
                    stringBuilder.append("{\"success\" : false, \"message\" :\"");
                    stringBuilder.append(u2fRegistrationForm.getErrors());
                    stringBuilder.append("\"}");
                }
            } else {
                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.startU2fRegistration(request);
                if (u2fRegistrationForm.getErrors().isEmpty()) {
                    stringBuilder.append("{\"success\" : true, \"message\" :");
                    stringBuilder.append(request.getSession().getAttribute("registrationChallenge"));
                    stringBuilder.append("}");
                } else {
                    stringBuilder.append("{\"success\" : false, \"message\" :\"");
                    stringBuilder.append(u2fRegistrationForm.getErrors());
                    stringBuilder.append("\"}");
                }
            }
            out.write(stringBuilder.toString());
        }
    }
}

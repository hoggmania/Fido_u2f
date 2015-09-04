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

@WebServlet("/adminAjaxRegistration")
public class AdminAjaxU2FRegistrationServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

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

        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/ajaxRegistration.jsp").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        //si l'utilisateur n'est pas connecté ou alors n'est pas l'admin on demande une redirection vers notLogged
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            stringBuilder.append("{ \"success\" : false, \"redirect\" :\"/notLogged\"}");
        }
        // Sinon si l'administrateur n'a pas de clé on lui demande d'en enregistrer une
        else if(request.getSession().getAttribute("hasKey").equals(false)){

            stringBuilder.append("{ \"success\" : false, \"redirect\" :\"/adminU2fRegister\"}");
        }
        //Sinon on procède à l'enregistrement de la clé
        else{


            HttpSession session = request.getSession();

            if (request.getParameter("register") == null && session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null) {


                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if (u2fRegistrationForm.getErrors().isEmpty()) {
                    stringBuilder.append("{\"success\" : true, \"message\" :\"");
                    stringBuilder.append(/*Messages.*/"OK");
                    stringBuilder.append("\"}");
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

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
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


@WebServlet("/u2fRegister")
public class U2FRegistrationServlet extends HttpServlet {


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

        if(request.getSession().getAttribute("username") == null){
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if (request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if((Boolean)request.getSession().getAttribute("hasKey") && !(Boolean) request.getSession().getAttribute("u2fAuthenticated")){
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate"));
        }
        else{
            request.getSession().removeAttribute("registrationChallenge");
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fRegistration.jsp").forward(request,response);
            request.getSession().removeAttribute("from");

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
        if(request.getSession().getAttribute("username") == null){
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if (request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if((Boolean)request.getSession().getAttribute("hasKey") && !(Boolean) request.getSession().getAttribute("u2fAuthenticated")){
            response.sendRedirect(request.getContextPath().concat("/u2fAuthenticate"));
        }
        else {
            HttpSession session = request.getSession();

            if (session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null){


                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if (u2fRegistrationForm.getErrors().isEmpty()) {
                    request.setAttribute("success", true);
                    request.setAttribute("message", u2fRegistrationForm.getMessage());
                } else {
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                }
            }
            else{
                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.startU2fRegistration(request);
                if(u2fRegistrationForm.getErrors().isEmpty()){
                    request.setAttribute("registrationChallenge", request.getSession().getAttribute("registrationChallenge"));
                }
                else{
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                }
            }
            request.setAttribute("from", request.getParameter("from"));
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fRegistration.jsp").forward(request, response);
        }
    }// end doPost

}// end class

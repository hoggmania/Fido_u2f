package fr.neowave.servlets;

import fr.neowave.forms.U2fAuthenticationForm;
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

@WebServlet("/u2fAuthenticate")
public class U2FAuthenticationServlet extends HttpServlet {

    @Override
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

        if(request.getSession().getAttribute("username") == null) {
            request.getSession().setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("hasKey").equals(false)){
            response.sendRedirect(request.getContextPath().concat("/u2fRegister"));
        }
        else{
            request.getSession().removeAttribute("authenticationChallenge");
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fAuthentication.jsp").forward(request, response);
            request.getSession().removeAttribute("from");

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
        if(request.getSession().getAttribute("username") == null) {
            request.setAttribute("from", Messages.AUTHENTICATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/authentication?from=").concat(String.valueOf(request.getRequestURL())));
        }
        else if(request.getSession().getAttribute("hasKey").equals(false)){
            response.sendRedirect(request.getContextPath().concat("/u2fRegister"));
        }
        else{
            U2fAuthenticationForm u2fAuthenticationForm = new U2fAuthenticationForm();

            if (request.getSession().getAttribute("authenticationChallenge") != null && request.getParameter("response") != null){
                u2fAuthenticationForm.doU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){
                    request.setAttribute("success", true);
                    request.setAttribute("message", u2fAuthenticationForm.getMessage());
                }
                else{
                    request.setAttribute("errors", u2fAuthenticationForm.getErrors());
                }
            }
            else {

                u2fAuthenticationForm.startU2fAuthentication(request);
                if(u2fAuthenticationForm.getErrors().isEmpty()){
                    request.setAttribute("authenticationChallenge", request.getSession().getAttribute("authenticationChallenge"));
                }
                else{
                    request.setAttribute("errors", u2fAuthenticationForm.getErrors());
                }
            }
            request.setAttribute("from", request.getParameter("from"));
            this.getServletContext().getRequestDispatcher("/WEB-INF/user/u2fAuthentication.jsp").forward(request, response);
            request.getSession().removeAttribute("from");
        }
    }
}

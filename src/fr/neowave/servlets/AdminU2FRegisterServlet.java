package fr.neowave.servlets;

import fr.neowave.forms.U2fRegistrationForm;

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

@WebServlet("/adminU2fRegister")
public class AdminU2FRegisterServlet extends HttpServlet {


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

        if(request.getSession().getAttribute("username") != null && request.getSession().getAttribute("username").equals("admin")){
            request.setAttribute("username", request.getSession().getAttribute("tempUser"));
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
            request.getSession().removeAttribute("from");
            request.getSession().removeAttribute("tempUser");
        }else {
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
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
        HttpSession session = request.getSession();
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        } else{
            if(request.getParameter("register") == null && session.getAttribute("registrationChallenge") != null && request.getParameter("response") != null){

                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.doU2fRegistration(request);
                if(u2fRegistrationForm.getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/adminManage"));
                }
                else{
                    request.setAttribute("username", request.getParameter("username"));
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                    request.getSession().removeAttribute("registerChallenge");
                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
                    request.getSession().removeAttribute("from");
                }
            }else{

                U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
                u2fRegistrationForm.startU2fRegistration(request);
                request.setAttribute("username", request.getParameter("username"));
                if(!u2fRegistrationForm.getErrors().isEmpty()){
                    request.setAttribute("errors", u2fRegistrationForm.getErrors());
                }

                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/u2fRegistration.jsp").forward(request,response);
                request.getSession().removeAttribute("from");
            }
        }
    }
}

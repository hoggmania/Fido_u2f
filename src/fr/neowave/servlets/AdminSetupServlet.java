package fr.neowave.servlets;

import fr.neowave.forms.OptionsForm;
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
import java.util.HashMap;
import java.util.List;

@WebServlet("/adminSetup")
public class AdminSetupServlet extends HttpServlet {
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

        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{

            OptionsForm optionsForm = new OptionsForm();
            optionsForm.get();
            if(optionsForm.getErrors().isEmpty()){
                request.setAttribute("options", optionsForm.getObject());
            }
            else{
                request.setAttribute("errors", optionsForm.getErrors());
            }

            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/setup.jsp").forward(request,response);

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
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            u2fRegistrationForm.startU2fRegistration(request);
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{

            OptionsForm optionsForm = new OptionsForm();

            if(request.getParameter("action") != null && request.getParameter("action").equals("change")){
                optionsForm.change(request);

            } else if(request.getParameter("action") != null && request.getParameter("action").equals("deleteAll")){
                optionsForm.deleteAll();
                if(optionsForm.getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/disconnection"));
                    return;
                }


            }else if(request.getParameter("action") != null && request.getParameter("action").equals("resetOptions")){
                optionsForm.resetOptions();
                if(optionsForm.getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/adminSetup"));
                    return;
                }
            }else {
                request.setAttribute("errors", new HashMap<>().put("default", "error"));
            }



            if(optionsForm.getErrors().isEmpty()){
                response.sendRedirect(request.getContextPath().concat("/adminSetup"));
            }else{
                optionsForm.get();
                if(optionsForm.getErrors().isEmpty()){
                    request.setAttribute("options", optionsForm.getObject());
                }
                else{
                    request.setAttribute("options", optionsForm.getObject());
                    request.setAttribute("errors", optionsForm.getErrors());
                }

                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/setup.jsp").forward(request,response);
            }

        }
    }
}

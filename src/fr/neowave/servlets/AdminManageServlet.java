package fr.neowave.servlets;

import fr.neowave.forms.ManageForm;
import fr.neowave.forms.U2fRegistrationForm;
import fr.neowave.messages.Messages;
import sun.misc.BASE64Encoder;

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


@WebServlet("/adminManage")
public class AdminManageServlet extends HttpServlet {

    public AdminManageServlet() {
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

        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{

            ManageForm manageForm = new ManageForm();
            manageForm.showUsers(request);
            if(manageForm.getErrors().isEmpty()){
                request.setAttribute("users", manageForm.getUsers());
            }
            else{
                request.setAttribute("errors", manageForm.getErrors());
            }
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/manage.jsp").forward(request,response);

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
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            u2fRegistrationForm.startU2fRegistration(request);
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        else{
            ManageForm manageForm = new ManageForm();
            if(request.getParameter("deleteUser") != null){
                manageForm.deleteUser(request);
            }else if(request.getParameter("suspendUser") != null) {
                manageForm.suspendUser(request);
            }
            else if(request.getParameter("deleteToken") != null){
                manageForm.deleteUsersToken(request);
            }
            else if(request.getParameter("suspendToken") != null){
                manageForm.suspendUsersToken(request);
            }
            else if(request.getParameter("change") != null){
                manageForm.changePassword(request);
            }
            else if(request.getParameter("ajax") != null){
                if(request.getParameter("username") != null){
                    BASE64Encoder base64Encoder = new BASE64Encoder();
                    response.sendRedirect(request.getContextPath().concat("/adminAjaxRegistration?id=".concat(base64Encoder.encode(request.getParameter("username").getBytes()))));
                    return;
                }
            }
            else if(request.getParameter("addToken") != null){
                if(request.getParameter("username") != null){
                    BASE64Encoder base64Encoder = new BASE64Encoder();
                    request.getSession().setAttribute("tempUser", request.getParameter("username"));
                    response.sendRedirect(request.getContextPath().concat("/adminU2fRegister?id=".concat(base64Encoder.encode(request.getParameter("username").getBytes()))));
                    return;
                }
            }else{
                request.setAttribute("errors", new HashMap<String, String>().put("default", "bad value"));
                request.setAttribute("success", false);
            }

            manageForm.showUsers(request);
            if(!manageForm.getErrors().isEmpty()){
                request.setAttribute("errors", manageForm.getErrors());
                request.setAttribute("success", false);
            }
            else{
                request.setAttribute("success", true);
                request.setAttribute("message", manageForm.getMessage());
            }
            request.setAttribute("users", manageForm.getUsers());
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/manage.jsp").forward(request,response);

        }
    }
}

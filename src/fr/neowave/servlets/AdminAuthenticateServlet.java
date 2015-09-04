package fr.neowave.servlets;

import fr.neowave.forms.AuthenticationForm;
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

@WebServlet("/adminAuthentication")
public class AdminAuthenticateServlet extends HttpServlet {

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

        if(request.getSession().getAttribute("username") != null ){
            if(!request.getSession().getAttribute("username").equals("admin")) response.sendRedirect(request.getContextPath().concat("/notLogged"));
            else response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }else {
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request, response);
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

        if(request.getSession().getAttribute("username") != null ){
            if(!request.getSession().getAttribute("username").equals("admin")) response.sendRedirect(request.getContextPath().concat("/notLogged"));
            else response.sendRedirect(request.getContextPath().concat("/adminManage"));
        }
        else if(request.getParameter("username") == null || !request.getParameter("username").equals("admin")){
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

        }else{

            AuthenticationForm authenticationForm = new AuthenticationForm();
            authenticationForm.startAuthentication(request);

            if(authenticationForm.getErrors().isEmpty()){


                    if((Boolean) request.getSession().getAttribute("hasKey")){

                        request.getSession().setAttribute("from", Messages.ADMIN_NO_TOKEN_AUTHENTICATED);
                        response.sendRedirect(request.getContextPath().concat("/adminU2fAuthenticate"));
                    } else{
                        request.getSession().setAttribute("from", Messages.ADMIN_NO_TOKEN_REGISTERED);
                        response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));

                    }
            }
            else{
                request.setAttribute("errors", authenticationForm.getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/admin/passwordAuthentication.jsp").forward(request,response);

            }
        }


    }//end doPost

}//end AdminAuthenticateServlet

package fr.neowave.servlets;

import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet de la page activity
 */
@WebServlet("/activity")
public class AdminActivityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*
         * Gère les headers de la page
         */
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

        //si l'utilisateur n'est pas connecté ou alors n'est pas l'admin -> redirige vers la page not logged
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/notLogged"));
        }
        //Sinon si l'administrateur n'a pas de clé on lui demande d'en enregistrer une
        else if(request.getSession().getAttribute("hasKey").equals(false)){
            U2fRegistrationForm u2fRegistrationForm = new U2fRegistrationForm();
            u2fRegistrationForm.startU2fRegistration(request);
            request.getSession().setAttribute("from", Messages.U2F_TOKEN_REGISTRATION_NEEDED);
            response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
        }
        //Sinon on affiche les logs datés de moins de 5 min
        else{
            try {
                request.setAttribute("logs", DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getLoggerDao().listActivity());
            } catch (SQLException | ParseException e) {
                request.setAttribute("errors", new ArrayList<>().add(e.getMessage()));
            }
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/activity.jsp").forward(request,response);
        }

    }
}

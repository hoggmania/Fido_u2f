package fr.neowave.servlets;

import fr.neowave.beans.Logger;
import fr.neowave.beans.User;
import fr.neowave.forms.AuthenticationForm;
import u2f.data.messages.AuthenticateRequest;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

    private Logger logger;

    public AuthenticationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null){

            request.removeAttribute("request");
            request.getSession().removeAttribute("authenticateRequests");
            request.getSession().removeAttribute("tempUsername");

            this.getServletContext().getRequestDispatcher("/WEB-INF/passwordAuthentication.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

        if(request.getSession().getAttribute("username") == null) { //if user is not connected
            AuthenticationForm authenticationForm = new AuthenticationForm();


            HttpSession session = request.getSession();

            if (session.getAttribute("authenticateRequests") != null) { // if the authentication request has already been sent to the client
                //authenticationForm.doAuthentication(request);
                if (authenticationForm.getFormResponse().getErrors().isEmpty()) { // if there is no error
                    session.setAttribute("username", session.getAttribute("tempUsername"));
                    request.setAttribute("authenticateRequests", authenticationForm.getFormResponse().getMessage());
                    response.sendRedirect(request.getContextPath().concat("/index"));
                } else {
                    request.setAttribute("errors", authenticationForm.getFormResponse().getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/passwordAuthentication.jsp").forward(request, response);
                }
                session.removeAttribute("authenticateRequests");
                session.removeAttribute("tempUsername");

            } else { //else if the authentication request hasn't been sent

                session.removeAttribute("authenticateRequests");
                User user = authenticationForm.startAuthentication(request);
                if (authenticationForm.getFormResponse().getErrors().isEmpty()) { //if there is no error
                    if (user.getRegistrations().isEmpty()) { //if the user has no registered token
                        session.setAttribute("username", user.getUsername()); //log in
                        response.sendRedirect(request.getContextPath().concat("/index"));
                    } else { // if user has registered tokens

                        //authenticationForm.authChallenge(request);
                        if (authenticationForm.getFormResponse().getErrors().isEmpty()) {
                            session.setAttribute("tempUsername", user.getUsername());
                            session.setAttribute("authenticateRequests", authenticationForm.getFormResponse().getMessage());
                            request.setAttribute("request", authenticationForm.getFormResponse().getMessage());
                            this.getServletContext().getRequestDispatcher("/WEB-INF/passwordAuthentication.jsp").forward(request, response);
                        } else {
                            request.setAttribute("errors", authenticationForm.getFormResponse().getErrors());
                            this.getServletContext().getRequestDispatcher("/WEB-INF/passwordAuthentication.jsp").forward(request, response);
                        }
                    }
                } else {
                    session.setAttribute("username", null);
                    request.setAttribute("username", request.getParameter("username"));
                    request.setAttribute("errors", authenticationForm.getFormResponse().getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/passwordAuthentication.jsp").forward(request, response);
                }
            }
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
    }
}

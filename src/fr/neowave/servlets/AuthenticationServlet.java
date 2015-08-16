package fr.neowave.servlets;

import fr.neowave.beans.User;
import fr.neowave.forms.AuthenticationForm;
import u2f.data.messages.AuthenticateRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

    public AuthenticationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null){
            this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

        AuthenticationForm authenticationForm = new AuthenticationForm();


        HttpSession session = request.getSession();

        if(session.getAttribute("request") != null){

            session.removeAttribute("request");
        }
        else{

            session.removeAttribute("request");
            User user = authenticationForm.startAuthentication(request);
            if(authenticationForm.getFormResponse().getErrors().isEmpty()){
                if(user.getRegistrations().isEmpty()){
                    session.setAttribute("username", user.getUsername());
                    response.sendRedirect(request.getContextPath().concat("/index"));
                }
                else{

                    authenticationForm.authChallenge(request);
                    if(authenticationForm.getFormResponse().getErrors().isEmpty()){
                        session.setAttribute("request", authenticationForm.getFormResponse());
                        request.setAttribute("request", authenticationForm.getFormResponse().getMessage());
                        this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
                    }
                    else{
                        request.setAttribute("error", authenticationForm.getFormResponse().getErrors());
                        this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
                    }
                }
            }
            else {
                session.setAttribute("username", null);
                request.setAttribute("username", request.getParameter("username"));
                request.setAttribute("error", authenticationForm.getFormResponse().getErrors());
                this.getServletContext().getRequestDispatcher("/WEB-INF/authentication.jsp").forward(request, response);
            }
        }
    }
}

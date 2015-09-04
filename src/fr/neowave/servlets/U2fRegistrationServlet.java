package fr.neowave.servlets;

import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;


@WebServlet("/u2fRegistration")
public class U2fRegistrationServlet extends HttpServlet {

    public U2fRegistrationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        if(request.getSession().getAttribute("username") != null){
            request.getSession().removeAttribute("challengeRequest");
            try {
                List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username")));
                request.setAttribute("registrations", registrations);

            } catch (SQLException | ClassNotFoundException | ParseException e) {
                request.setAttribute("error", e.getMessage());
            }
            this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);
        }
        else{
            response.sendRedirect(request.getContextPath().concat("/index"));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{

        // if user is connected
        if(request.getSession().getAttribute("username") != null){

            //if we already send the request to the key
            if(request.getSession().getAttribute("challengeRequest") != null && request.getParameter("response") != null){


                U2fRegistrationForm U2fRegistrationForm = new U2fRegistrationForm();
                U2fRegistrationForm.doRegistration(request);
                if(U2fRegistrationForm.getFormResponse().getErrors().isEmpty()){
                    request.setAttribute("success", U2fRegistrationForm.getFormResponse().getMessage());
                    request.getSession().removeAttribute("challengeRequest");
                    response.sendRedirect("/fido/keyList");
                }
                else{
                    request.setAttribute("error", U2fRegistrationForm.getFormResponse().getErrors());
                    request.getSession().removeAttribute("challengeRequest");
                    this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);
                }



            }else { //if we haven't send the request to the key
                U2fRegistrationForm U2fRegistrationForm = new U2fRegistrationForm();
                U2fRegistrationForm.regChallenge(request);

                // if there is no error
                if(U2fRegistrationForm.getFormResponse().getErrors().isEmpty()){

                    request.getSession().setAttribute("challengeRequest", U2fRegistrationForm.getFormResponse().getMessage());
                    request.setAttribute("request", U2fRegistrationForm.getFormResponse().getMessage());

                    this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);

                }
                else{ // if there is error(s)
                    request.setAttribute("error", U2fRegistrationForm.getFormResponse().getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);
                } // end if

            }// end if

        } else{ //if user not connected
            response.sendRedirect(request.getContextPath().concat("/index"));
        }// end if

    }// end doPost

}// end class

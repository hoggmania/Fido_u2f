package fr.neowave.servlets;

import fr.neowave.forms.AddKeyForm;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/keyList")
public class KeyListServlet extends HttpServlet {

    public KeyListServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        if(request.getSession().getAttribute("username") != null){
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
            if(request.getSession().getAttribute("challengeRequest") != null){


                AddKeyForm addKeyForm = new AddKeyForm();
                addKeyForm.doRegistration(request);
                if(addKeyForm.getFormResponse().getErrors().isEmpty()){
                    request.setAttribute("success", addKeyForm.getFormResponse().getMessage());
                }
                else{
                    request.setAttribute("error", addKeyForm.getFormResponse().getErrors());
                }
                request.getSession().removeAttribute("challengeRequest");
                this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);

            }else { //if we haven't send the request to the key
                AddKeyForm addKeyForm = new AddKeyForm();
                addKeyForm.regChallenge(request);

                // if there is no error
                if(addKeyForm.getFormResponse().getErrors().isEmpty()){

                    request.getSession().setAttribute("challengeRequest", addKeyForm.getFormResponse().getMessage());
                    request.setAttribute("request", addKeyForm.getFormResponse().getMessage());

                    this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);

                }
                else{ // if there is error(s)
                    request.setAttribute("error", addKeyForm.getFormResponse().getErrors());
                    this.getServletContext().getRequestDispatcher("/WEB-INF/keyList.jsp").forward(request, response);
                } // end if

            }// end if

        } else{ //if user not connected
            response.sendRedirect(request.getContextPath().concat("/index"));
        }// end if

    }// end doPost

}// end class

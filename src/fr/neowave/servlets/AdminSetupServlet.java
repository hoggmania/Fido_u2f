package fr.neowave.servlets;

import fr.neowave.forms.OptionsForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/adminSetup")
public class AdminSetupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        }
        else{
            if(!Boolean.valueOf(String.valueOf(request.getSession().getAttribute("hasKey")))) {
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
            response.sendRedirect(request.getContextPath().concat("/404"));
        }
        else{
            if(!Boolean.valueOf(String.valueOf(request.getSession().getAttribute("hasKey")))) {
                response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
            }
            else{

                OptionsForm optionsForm = new OptionsForm();

                if(request.getParameter("action") != null && request.getParameter("action").equals("change")){
                    optionsForm.change(request);

                }else if(request.getParameter("action") != null && request.getParameter("action").equals("reset")){
                    optionsForm.rollback();

                }else if(request.getParameter("action") != null && request.getParameter("action").equals("resetAll")){

                    optionsForm.rollbackAll();


                } else {
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
                        request.setAttribute("errors", optionsForm.getErrors());
                    }

                    this.getServletContext().getRequestDispatcher("/WEB-INF/admin/setup.jsp").forward(request,response);
                }


            }
        }
    }
}

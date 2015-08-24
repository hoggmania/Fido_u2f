package fr.neowave.servlets;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.neowave.forms.ManageForm;
import fr.neowave.forms.U2fRegistrationForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


@WebServlet("/adminManage")
public class AdminManageServlet extends HttpServlet {

    public AdminManageServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")
                || request.getSession().getAttribute("hasKey").equals(false)){
            response.sendRedirect(request.getContextPath().concat("/404"));
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
        if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")
                || request.getSession().getAttribute("hasKey").equals(false)){
            response.sendRedirect(request.getContextPath().concat("/404"));
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
            else if(request.getParameter("addToken") != null){
                U2fRegistrationForm registrationForm = new U2fRegistrationForm();
                registrationForm.startU2fRegistration(request);
                if(manageForm.getErrors().isEmpty()){
                    response.sendRedirect(request.getContextPath().concat("/adminU2fRegister"));
                    return;
                }
            }else{
                request.setAttribute("errors", new HashMap<String, String>().put("default", "c pa bn"));
            }

            manageForm.showUsers(request);
            if(!manageForm.getErrors().isEmpty()){
                request.setAttribute("errors", manageForm.getErrors());
            }
            request.setAttribute("users", manageForm.getUsers());
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin/manage.jsp").forward(request,response);

        }
    }
}

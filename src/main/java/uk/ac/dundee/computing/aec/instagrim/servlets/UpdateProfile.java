/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "UpdateProfile", urlPatterns = {"/updateprofile/*"})
public class UpdateProfile extends HttpServlet {
    Cluster cluster=null;
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }




    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //If the user pressed the submit button
        if(request.getParameter("update") != null){
            String username=request.getParameter("username");
            String password=request.getParameter("password");
            String firstname=request.getParameter("firstname");
            String lastname=request.getParameter("lastname");
            String email=request.getParameter("email");
            String sex=request.getParameter("sex");
            String dob=request.getParameter("dob");
            String streetname=request.getParameter("streetname");
            String city=request.getParameter("city");
            String zip=request.getParameter("zip");
            String country=request.getParameter("country");
            HttpSession session=request.getSession();
            LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
            User us=new User();
            us.setCluster(cluster);
            us.updateUser(lg.getUsername(), password,firstname,lastname,email,sex,dob,streetname,city,zip,country);
            //Send them to the profile page of the user
            response.sendRedirect("/Instagrim/profiles" + lg.getUsername());
        }
        
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        String args[] = Convertors.SplitRequestPath(request); //Borrowed from Image, takes arguments of URL and splits it so we can get the username
        String username = args[2];
        if(lg != null){
            if (lg.getlogedin()){
                //If the user who is logged in is on their own updateprofile page
                if(lg.getUsername().equals(username)){
                        //Create new userobject called usersprofile
                        User usersprofile = new User(); 
                        usersprofile.setCluster(cluster); //Set the cluster in this user class to the one we are sending
                        Map<String,String> userinfo = usersprofile.UserInfoMap(username); //Set our map to the one we get from the user model, which is retrieved from the database
                        RequestDispatcher rd = request.getRequestDispatcher("/updateprofile.jsp"); //Get the request dispatcher from useprrofile
                        request.setAttribute("InfoMap", userinfo); //Set the attribute of infomap to the map we just created
                        request.setAttribute("user", username);
                        rd.forward(request, response);
                        } else {

                        }
                    } else {
                        response.sendRedirect("/login.jsp");
                    }
        } else {
            response.sendRedirect("/Instagrim/login.jsp");
        } 
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

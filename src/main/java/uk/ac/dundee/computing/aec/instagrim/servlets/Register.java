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
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {
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
        User us=new User();
        us.setCluster(cluster);
        us.RegisterUser(username, password,firstname,lastname,email,sex,dob,streetname,city,zip,country);
        
	response.sendRedirect("/Instagrim");
        
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        if(lg != null){
            if (lg.getlogedin()){
                String args[] = Convertors.SplitRequestPath(request); //Borrowed from Image, takes arguments of URL and splits it so we can get the username
                String username = args[2];
                User usersprofile = new User(); //Create new user object
                usersprofile.setCluster(cluster); //Set the cluster in this user class to the one we are sending
                Map<String,String> userinfo = usersprofile.UserInfoMap(username); //Set our map to the one we get from the user model, which is retrieved from the database
                RequestDispatcher rd = request.getRequestDispatcher("/userprofile.jsp"); //Get the request dispatcher from useprrofile
                request.setAttribute("InfoMap", userinfo); //Set the attribute of infomap to the map we just created
                boolean loggedin = true;
                request.setAttribute("loggedin", loggedin);
                String logedinname=lg.getUsername();
                Set<String> set = usersprofile.followerSet(username);
                request.setAttribute("followerSet", set);
                boolean isfollowing = usersprofile.isFollowing(logedinname,username);
                request.setAttribute("isfollowing", isfollowing);
                rd.forward(request, response);
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

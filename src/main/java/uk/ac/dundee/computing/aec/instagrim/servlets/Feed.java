/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Map;
import java.util.List;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
/**
 *
 * @author Administrator
 */
@WebServlet(name = "Feed", urlPatterns = {"/feed"})
public class Feed extends HttpServlet {

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
               HttpSession session=request.getSession();
               
               
    }
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        //If there is a loggedin bean
        if(lg != null){
            //If the user is logged in
            if (lg.getlogedin()){
                    //Create new userobject called usersprofile
                    PicModel pm = new PicModel();
                    pm.setCluster(cluster); //Set the cluster in this user class to the one we are sending
                    List<Pic> feed = pm.getUserFeed(lg.getUsername()); //Set our map to the one we get from the user model, which is retrieved from the database
                    RequestDispatcher rd = request.getRequestDispatcher("/feed.jsp"); //Get the request dispatcher from useprrofile
                    request.setAttribute("UserFeed", feed); 
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

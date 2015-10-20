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
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


/**
 *
 * @author Administrator
 */
@WebServlet(name = "UserProfiles", urlPatterns = {"/profiles/*"})
public class UserProfiles extends HttpServlet {

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
               LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
               String username; //Declare username
               
               if (request.getParameter("Follow") != null ) //If the user clicked "follow"
               {
                    if (lg.getlogedin()){
                        username=lg.getUsername(); //Set the variable username to the users logged in username
                        String args[] = Convertors.SplitRequestPath(request); //Borrowed from Image, takes arguments of URL and splits it so we can get the username
                        String followtarget = args[2]; //This is our target for following
                        
                        User user = new User();
                        user.setCluster(cluster);
                        user.followUser(username,followtarget);
                        //RequestDispatcher rd=request.getRequestDispatcher("/profiles/" + followtarget);
                       // rd.forward(null,response); 
                         response.sendRedirect(request.toString()); //We aren't really interested in the request or the response, in fact a request will lead to an infinite loop, so we redirect here.
                        
                    } else {
                        //Send them to the login page
                        response.sendRedirect("/Instagrim/login.jsp");
                    }
                
        
                }
               
               if (request.getParameter("UnFollow") != null ){ //If the user clicked "unfollow"
                   
                  if (lg.getlogedin()){
                        username=lg.getUsername(); //Set the variable username to the users logged in username
                        String args[] = Convertors.SplitRequestPath(request); //Borrowed from Image, takes arguments of URL and splits it so we can get the username
                        String followtarget = args[2]; //This is our target for following
                        
                        User user = new User();
                        user.setCluster(cluster);
                        user.unfollowUser(username,followtarget);
                        //RequestDispatcher rd=request.getRequestDispatcher("/profiles/" + followtarget);
                       // rd.forward(null,response); 
                         response.sendRedirect(request.toString()); //We aren't really interested in the request or the response, in fact a request will lead to an infinite loop, so we redirect here.
                        
                  }

               }
               
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

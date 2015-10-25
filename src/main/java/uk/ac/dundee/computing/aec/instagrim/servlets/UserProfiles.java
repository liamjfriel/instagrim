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
                        //Make user object
                        User user = new User();
                        //Set the cluster in the user object to the cluster we pass
                        user.setCluster(cluster);
                        //Call the follower user method in user class
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
                //Set the string username to part of the URL
                String username = args[2];
                //Create new user object
                User usersprofile = new User(); 
                //Set the cluster in the userprofile object to the cluster we initialised in init
                usersprofile.setCluster(cluster);
                //Map object of type string, string called userinfo equals the return of the user info map from user model
                Map<String,String> userinfo = usersprofile.UserInfoMap(username); 
                //Request dispatcher for userprofile.jsp
                RequestDispatcher rd = request.getRequestDispatcher("/userprofile.jsp"); //Get the request dispatcher from useprrofile
                //Set the attribute "InfoMap" that will be  called from userprofile.jsp to the info m ap we got 
                request.setAttribute("InfoMap", userinfo); //Set the attribute of infomap to the map we just created
                //Boolean logged in equals true
                boolean loggedin = true;
                //Set the attribute "loggedin" to the boolean we just set, can't use .getlogedin() for some reason
                request.setAttribute("loggedin", loggedin);
                //String logedinname equals the name of the user
                String logedinname=lg.getUsername();
                //Set of type string is set to the return of the function which gets all the followers of the username passed
                Set<String> set = usersprofile.followerSet(username);
                //Attribute "followerSet" is set equal to set so it can be passed to userprofile.jsp
                request.setAttribute("followerSet", set);
                //Boolean isfollowing equals the return the function in the model that checks if the logged in user is already following the user who's profile they are visiting
                boolean isfollowing = usersprofile.isFollowing(logedinname,username);
                //Attrivute "isfollowing" is set to that boolean so it can be passed to the jsp
                request.setAttribute("isfollowing", isfollowing);
                //Forward
                rd.forward(request, response);
                } else {
                //The user isn't logged in, so we're telling them to log in
                response.sendRedirect("/login.jsp");
            }
        //The user isn't logged in, so we're telling them to go log in
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
        return "This servlet controls the userprofiles.";
    }// </editor-fold>

}

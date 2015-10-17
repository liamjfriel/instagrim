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
@WebServlet(name = "Search", urlPatterns = {"/Search"})
public class Search extends HttpServlet {

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
        
        if (request.getParameter("SubmitSearch") != null ) //If the user clicked "follow"
               {
                String querytype=request.getParameter("searchby");
                String usernametopass=request.getParameter("searchquery");
                RequestDispatcher rd = request.getRequestDispatcher("/search.jsp");
                
                switch(querytype){
                    case "byname":
                    {
                        User user = new User();
                        user.setCluster(cluster);
                        List<Map> nameresultmap = user.userSearch("login",usernametopass);
                        request.setAttribute("searchtype", querytype);
                        request.setAttribute("searchresult", nameresultmap);
                        
                        break;
                        
                    }
                    case "picbyname":
                    {
                        PicModel picmod = new PicModel();
                        picmod.setCluster(cluster);
                        List<Pic> picresultmap = picmod.getPicsForUser(usernametopass);
                        request.setAttribute("searchtype", querytype);
                        request.setAttribute("searchresult", picresultmap);
                        break;
                        
                    }
                    
                }
                
                rd.forward(request, response);
                
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

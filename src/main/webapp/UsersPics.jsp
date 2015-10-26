<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <%-- JQUERY AND JQUERY UI SOURCED FROM http://jqueryui.com/download/#!version=1.9.2, DOES UI WORK LIKE CALENDER--%>
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <link rel="stylesheet" type="text/css" href="/Instagrim/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="/Instagrim/css/mystyle.css" />
    <head>
        <title>Instagrim</title>
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header>
       
        
          <nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="container">
              <div class="navbar-header">
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
                <a class="navbar-brand" href="#">Instagrim</a>
              </div>
              <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
            <%

            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            if (lg != null) {
                String UserName = lg.getUsername();
                if (lg.getlogedin()) {
            %>
                  <li class="active"><a href="#">Home</a></li>
                  <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                  <li><a href="/Instagrim/feed">Feed</a></li>
                  <li><a href="<%=lg.getUsername()%>">Profile</a></li>
                  <li><a href="/Instagrim/updateprofile/<%=lg.getUsername()%>">Update profile</a></li>
                  <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a></li>
                  <li><a href="/Instagrim/logout.jsp">Logout</a></li>             
                <%}else{
                                %>
                    <li><a href="/Instagrim/register.jsp">Register</a></li>
                    <li><a href="/Instagrim/login.jsp">Login</a></li>
                  
                <%
                      }
                } else {
                  %>
                  <li><a href="/Instagrim/register.jsp">Register</a></li>
                  <li><a href="/Instagrim/login.jsp">Login</a></li>
                  <%            
                }
            %>
            <li><a href="/Instagrim/search.jsp">Search</a></li>
               </ul>
              </div><!--/.nav-collapse -->
            </div>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
        <div class="container">
            <div class="main-container">
                <h1>Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();

        %>
        <a href="/Instagrim/Imagepage/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>
        <br/>
        <%

            }
            }
        %>
            </div>
        </div>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>

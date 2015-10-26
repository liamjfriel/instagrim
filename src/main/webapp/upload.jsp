<%-- 
    Document   : upload
    Created on : Sep 22, 2014, 6:31:50 PM
    Author     : Administrator
--%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%-- JQUERY AND JQUERY UI SOURCED FROM http://jqueryui.com/download/#!version=1.9.2, DOES UI WORK LIKE CALENDER--%>
    <script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="css/mystyle.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
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
            <div class="container">
            <div class="main-container">
                <h3>File Upload</h3>
                <form method="POST" enctype="multipart/form-data" action="Image">
                    File to upload:</br> 
                    <center><input type="file" name="upfile"><br/></center>

                    <input type="submit" value="Press"> to upload the file!<br/>
                    <input type="radio" name="radios" value="Blackwhite" checked="checked">Black and white<br>
                    <input type="radio" name="radios" value="Normal">Normal filter<br>
                  
                </form>
            </div>
            </div>
        </article>
        <footer>
            <ul>
                <li class="footer">Liam Friel</li>
            </ul>
        </footer>
    </body>
</html>

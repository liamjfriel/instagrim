<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
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
                  <li><a href="upload.jsp">Upload</a></li>
                  <li><a href="feed">Feed</a></li>
                  <li><a href="profiles/<%=lg.getUsername()%>">Profile</a></li>
                  <li><a href="Images/<%=lg.getUsername()%>">Your Images</a></li>
                  <li><a href="logout.jsp">Logout</a></li>             
                <%}else{
                                %>
                  <li><a href="register.jsp">Register</a></li>
                  <li><a href="login.jsp">Login</a></li>
                  
                <%
                }            
            }
            %>
            <li><a href="search.jsp">Search</a></li>
               </ul>
              </div><!--/.nav-collapse -->
            </div>
        </nav>
       
        <article>
        <div class="container">
        <div class="main-container">
            <h3>Login</h3>
            <form method="POST"  action="Login">
                <p>
                    User Name<br/> <input type="text" name="username"></br>
                    Password</br><input type="password" name="password">
                </p>
                <br/>
                <input type="submit" value="Login"> 
            </form>
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

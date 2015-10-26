<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
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
        <%-- https://github.com/mrmarkfrench/country-select-js --%>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script src="build/js/countrySelect.min.js"></script>
        
        <link rel="stylesheet" type="text/css" href="css/mystyle.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
             <script>
                $(function() {
                $( "#datepicker" ).datepicker();
                });
                
            </script>
            <script>
                   $(function() {
                    $("#country").countrySelect();
                    });
            </script>
        </header>
        
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
                  <li><a href="updateprofile/<%=lg.getUsername()%>">Update profile</a></li>
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
            <h3>Register as user</h3>
            <form method="POST"  action="Register">
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                    <li>First Name <input type="text" name="firstname"></li>
                    <li>Last Name <input type="text" name="lastname"></li>
                    <li>Email <input type="text" name="email"></li>
                    <li>Date of Birth <input type="text" id="datepicker" name="dob"></li>
                    <li>Sex 
                        <select name="sex">
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="prefernototsay">Prefer not to say..</option>
                        </select>
                    </li>
                    <li>Street Name <input type="text" name="streetname"></li>
                    <li>City <input type="text" name="city"></li>
                    <li>Zip <input type="text" name="zip"></li>
                   
                    <li>Nationality<input type="text" name="country" id="country_selector">
                    <script>
  
                    </script>
                      
                    </li>
                        
                        
                    <li><input type="submit" value="Submit"></li>
                </ul>
                <br/>
                <input type="submit" value="Register"> 
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                
            </ul>
        </footer>
    </body>
</html>

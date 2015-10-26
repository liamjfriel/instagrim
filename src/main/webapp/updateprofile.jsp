<%-- 
    Document   : updateprofile.jsp
    Created on : October, 2015
    Author     : Liam Friel
--%>

<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <%-- JQUERY AND JQUERY UI SOURCED FROM http://jqueryui.com/download/#!version=1.9.2, DOES UI WORK LIKE CALENDER--%>
        <script src="/Instagrim/js/jquery-1.8.3.js"></script>
        <script src="/Instagrim/js/countrySelect.min.js"></script>
        <link rel="stylesheet" href="/Instagrim/css/countrySelect.min.css">
        <script src="js/jquery-ui-1.9.2.custom.js"></script>
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/mystyle.css" />
        
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
            <h3>Update your profile</h3>
        <%
            String username = (String) request.getParameter("user");
            java.util.HashMap<String,String> userinfo = (java.util.HashMap<String,String>) request.getAttribute("InfoMap");
            if (userinfo == null) {
            //This should never happen but we're putting it in anyway
        %>
            <p>You're trying change a profile that doesn't exist!</p>
        <%
            } else {
        %>
            <form method="POST"  action="UpdateProfile">
                <ul>
                    <li>User Name: <%=username%></li>
                    <li><b>YOU MUST CHANGE THIS</b> Password <input type="password" name="password"></li>
                    <li>First Name <input type="text" name="firstname" value="<%=userinfo.get("FirstName")%>"></li>
                    <li>Last Name <input type="text" name="lastname" value="<%=userinfo.get("LastName")%>"></li>
                    <li>Email <input type="text" name="email" value="<%=userinfo.get("Email")%>"></li>
                    <li>Date of Birth <input type="text" id="datepicker" name="dob" value="<%=userinfo.get("DOB")%>"></li>
                    <li>Sex 
                        <select name="sex">
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="prefernototsay">Prefer not to say..</option>
                        </select>
                    </li>
                    <li>Street Name <input type="text" name="streetname" value="<%=userinfo.get("Street")%>"></li>
                    <li>City <input type="text" name="city" value="<%=userinfo.get("City")%>"></li>
                    <li>Zip <input type="text" name="zip" value="<%=userinfo.get("Zip")%>"></li>
                   
                    <li>Nationality<input type="text" name="country" id="country_selector" value="<%=userinfo.get("Country")%>">
                    <script>
                    $(function() {
                    $("#country").countrySelect();
                    });
                    </script>
                      
                    </li>
                        
                        
                    <li><input type="submit" name="update" value="Submit"></li>
            <%
            }
            %>
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

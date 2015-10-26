<%-- 
    Document   : UserProfile
    Created on : 13/10/15
    Author     : Liam Friel
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
    
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/mystyle.css" />
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
                <h1>Your profile</h1>
        <%
            String username = request.getParameter("user");
            java.util.HashMap<String,String> userinfo = (java.util.HashMap<String,String>) request.getAttribute("InfoMap");
            if (userinfo == null) {
        %>
        <p>User profile does not exist!</p>
        <%
        } else {
                //If the user hsa not set a profile picture yet
                if(userinfo.get("ProfilePic") == null)
                {
                //Tell the user that they have not set a profile picture and should do so
        %>
        <p>You have not set a profile picture! Go to one of your images and press "make profile picture"!</p><br/>
        <%
                } else {
                //Show the profile picture picture
        %>
        <p>Profile picture:</p><br/>
        <p><a href="/Instagrim/Imagepage/<%=userinfo.get("ProfilePic")%>"><img src="/Instagrim/Thumb/<%=userinfo.get("ProfilePic")%>"></a></p><br/>
        <%
                }
        %>
        <p>First Name: <%=userinfo.get("FirstName")%></p><br/>
        <p>Second Name: <%=userinfo.get("SecondName")%></p><br/>
        <p>Sex: <%=userinfo.get("Sex")%></p><br/>
        <p>DOB: <%=userinfo.get("DOB")%></p><br/>
        <p>Town: <%=userinfo.get("Town")%></p><br/>
        <p>Country: <%=userinfo.get("Country")%></p><br/>
        <br/>
       <%
        }
            boolean loggedin = (boolean)request.getAttribute("loggedin");
            if(loggedin){
                boolean isfollowing = (boolean)request.getAttribute("isfollowing");
                if(isfollowing){
        %>
                <form method="POST">
                    <input type="submit" name="UnFollow" value="unfollow"> 
                </form><br/>
        <%
                } else { 
        %>  
                <form method="POST">
                    <input type="submit" name="Follow" value="follow"> 
                </form><br/>
        <%
                }
                    java.util.Set<String> followerset = (java.util.Set<String>) request.getAttribute("followerSet");
                    if(followerset == null){
        %>
            <p>User has no followers.</p>
        <%
                } else { 
        %>
            <p> Followers:</p>
            <p>
        <% 
                    Iterator<String> iterator;
                    iterator = followerset.iterator();

                    while (iterator.hasNext()) {
                    String follower = (String) iterator.next();
        %> 
                    <%=follower%> 
        <%
                    }
                    %>
            </p>
                    <%
                }
            } else {
                
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

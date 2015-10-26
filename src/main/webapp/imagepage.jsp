<%-- 
    Document   : Imagepages
    Created on : October 2015
    Author     : Liam Friel
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.lib.PicComment" %>
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
        <header>
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
           
        <div class="container">
            <div class="main-container">
        <%
            Pic pictoshow = (Pic) request.getAttribute("pic");
            if (pictoshow == null)
            {
        %>
        <p>No Picture found</p>
        <%
        } else {
           
        %>
        <h1>By <%=pictoshow.getUploader()%> on <%=pictoshow.getUploaddate()%></h1>
        <img src="/Instagrim/Image/<%=pictoshow.getSUUID()%>"><br/>
            <form method="POST">
                <input type="text" name="commentext"> 
                <input type="submit" name="commentsubmit" value="Submit"> 
            </form>
        <%
            Date datetopass = pictoshow.getUploaddate();
            request.setAttribute("picdate",datetopass);
            boolean canbeprofilepic = (boolean) request.getAttribute("canprofilepic");
            //If canbeprofilepic is true
            if(canbeprofilepic)
            {
                //Then show the button that would allow the user to make this their profile pic
        %>
                <form method="POST">
                <input type="submit" name="makeprofile" value="Make profile picture"> 
                <input type="submit" name="deletepic" value="Delete picture">
               
                </form>
         
        <%
            }
            java.util.LinkedList<Map> commentlist = (java.util.LinkedList<Map>) request.getAttribute("comments");
            Iterator<Map> iterator;
            iterator = commentlist.iterator();
            while (iterator.hasNext()) {
                Map<Date,PicComment> commentmap = (Map) iterator.next();
                
                    for (Map.Entry<Date,PicComment> entry : commentmap.entrySet())
                    {
        %>
                    <p><h5>Comment by <%=entry.getValue().getAuthor()%> on <%=entry.getKey()%>:</h5></p>
                    <p><h5><%=entry.getValue().getComment()%></h5></p>
       <%
                    }
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

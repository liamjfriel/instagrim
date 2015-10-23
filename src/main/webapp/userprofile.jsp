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
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
    </head>
    <body>
        <header>
        
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
        </header>
        
        <nav>
            <ul>
                <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li>
            </ul>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
            
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
        <p>You have not set a profile picture! Go to one of your images and press "make profile picture"!</p>
        <%
                } else {
                //Show the profile picture picture
        %>
        <p>Profile picture:</p>
        <a href="/Instagrim/Image/<%=userinfo.get("ProfilePic")%>"><img src="/Instagrim/Thumb/<%=userinfo.get("ProfilePic")%>"></a>
        <%
                }
        %>
        <p>First Name: <%=userinfo.get("FirstName")%></p>
        <p>Second Name: <%=userinfo.get("SecondName")%></p>
        <p>Sex: <%=userinfo.get("Sex")%></p>
        <p>DOB: <%=userinfo.get("DOB")%></p>
        <p>Town: <%=userinfo.get("Town")%></p>
        <p>Country: <%=userinfo.get("Country")%></p>
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
                </form>
        <%
                } else { 
        %>  
                <form method="POST">
                    <input type="submit" name="Follow" value="follow"> 
                </form>
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
        <% 
                    Iterator<String> iterator;
                    iterator = followerset.iterator();

                    while (iterator.hasNext()) {
                    String follower = (String) iterator.next();
        %> 
            <p><%=follower%></p>
        <%
                    }
                }
            } else {
                
            }
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>

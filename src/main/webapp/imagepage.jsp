<%-- 
    Document   : Imagepages
    Created on : October 2015
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
        <input type="text" name="commentext"> <input type="submit" name="Comment" value="Submit"> 
        <%

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

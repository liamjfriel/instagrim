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
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>

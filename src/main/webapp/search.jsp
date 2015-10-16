<%-- 
    Document   : search
    Created on : 15/10/15
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
        
            <form method="POST"  action="Search">
                <ul>
                    <li>
                        <input type="text" name="searchquery">
                        <select name="searchby">
                            <option value="byname">by User Name</option>
                            <option value="bysex">by Sex</option>
                            <option value="byemail">by Email</option>
                        </select>
                    </li>
                    <li><input type="submit" name="SubmitSearch" value="Submit"></li>
                    
                </ul>
            </form>
            
        <%
            java.util.List<Map> results = (java.util.List<Map>) request.getAttribute("searchresult");
            if(results == null){
        %>
        <p>No results.</p>
        <%
            } else {
            Iterator<Map> iterator;
            iterator = results.iterator();   
        %>
        <ul>
        <%
            while(iterator.hasNext()){ 
            Map result = (Map) iterator.next();  
            
        %>
        <li>
            <a href="/Instagrim/Image/<%=result.get("profilepicid")%>"><img src="/Instagrim/Thumb/<%=result.get("profilepicid")%>" height="50" width="50"></a>
            <a href="/Instagrim/profiles/<%=result.get("login")%>"><%=result.get("login")%></a>
            Sex: <%=result.get("sex")%>
        </li>   
       <%
            }
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

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
        <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="css/mystyle.css" />
    </head>
    <body>
        <header>
        
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
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
                  <li><a href="Images/<%=lg.getUsername()%>">Your Images</a></li>
                  <li><a href="logout.jsp">Logout</a></li>             
                <%}else{
                                %>
                    <li><a href="register.jsp">Register</a></li>
                    <li><a href="login.jsp">Login</a></li>
                  
                <%
                      }
                } else {
                  %>
                  <li><a href="register.jsp">Register</a></li>
                  <li><a href="login.jsp">Login</a></li>
                  <%            
                }
            %>
            <li><a href="search.jsp">Search</a></li>
               </ul>
              </div><!--/.nav-collapse -->
            </div>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
        
            <form method="POST"  action="Search">
                <ul>
                    <li>
                        <input type="text" name="searchquery">
                        <select name="searchby">
                            <option value="byname">profiles by Username</option>
                            <option value="picbyname">pictures by Username</option>
                        </select>
                    </li>
                    <li><input type="submit" name="SubmitSearch" value="Submit"></li>
                    
                </ul>
            </form>
            
        <%
            String searchtype = (String) request.getAttribute("searchtype");
            System.out.println(searchtype);
            if(searchtype!= null){
                switch(searchtype)
                {
                    case "byname":
                    {
                        java.util.List<Map> results = (java.util.List<Map>) request.getAttribute("searchresult");
                        if(results != null)
                        {
                            Iterator<Map> iterator;
                            iterator = results.iterator();   
        %>
                    <ul>
                    <%
                            while(iterator.hasNext()){ 
                            Map result = (Map) iterator.next();  
                        
                    %>
                        <li>
                            <%
                            //Check of the user has a profile pic, if they don't we won't display anything
                            if(result.get("profilepicid") != null){%>
                            <a href="/Instagrim/Image/<%=result.get("profilepicid")%>"><img src="/Instagrim/Thumb/<%=result.get("profilepicid")%>" height="50" width="50"></a>
                        <%
                            }
                        %>
                                <a href="/Instagrim/profiles/<%=result.get("login")%>"><%=result.get("login")%></a>
                                <i><%=result.get("description")%></i>
                                Sex: <%=result.get("sex")%>
                            </li>   
                    <%      }
                        }
                    %>
                    </ul>
    <%
                        break;
                    }
                    case "picbyname":
                    {
                        java.util.List<Pic> picresults = (java.util.List<Pic>) request.getAttribute("searchresult");
                        if(picresults != null){
                            Iterator<Pic> iterator;
                            iterator = picresults.iterator();
        %>
                        <ul>
        <%
                            while (iterator.hasNext()) {
                                Pic p = (Pic) iterator.next(); 
        %>
                            <li>
                                <a href="/Instagrim/Image/<%=p.getSUUID()%>"><img src="/Instagrim/Thumb/<%=p.getSUUID()%>" height="50" width="50"></a>
                                <a href="/Instagrim/profiles/<%=p.getUploader()%>">by <%=p.getUploader()%></a>
                                on <%=p.getUploaddate()%>
                            </li>             
        <%
                            }

        %>
                         </ul>
        <%                 
                        }
                        break; 
                    }
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

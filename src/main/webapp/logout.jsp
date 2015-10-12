<%-- 
    Document   : logout
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <h1>Logging out..</h1>
        <% 
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null) {
                            String UserName = lg.getUsername();
                            if (lg.getlogedin()) {
                                lg.setLoginState(false);
                                lg.setUsername(null);
                            } else { }
                        } else { }
            response.sendRedirect("index.jsp");
        %>
    </body>
</html>

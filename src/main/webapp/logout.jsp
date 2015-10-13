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
        <form id="postme" method="POST"  action="Logout">
            <script type="text/javascript">
                document.getElementById("postme").submit();
            </script>
        </form>
    </body>
</html>

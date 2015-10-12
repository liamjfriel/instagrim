<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <%-- JQUERY AND JQUERY UI SOURCED FROM http://jqueryui.com/download/#!version=1.9.2, DOES UI WORK LIKE CALENDER--%>
        <script src="js/jquery-1.8.3.js"></script>
        <script src="js/countrySelect.min.js"></script>
        <link rel="stylesheet" href="css/countrySelect.min.css">
        <script src="js/jquery-ui-1.9.2.custom.js"></script>
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
        
        <nav>
            <ul><script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
            <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
            <script type="text/javascript" src="js/index.js"></script>
                
            <li><a href="/Instagrim/Images/majed">Sample Images</a></li>
            </ul>
        </nav>
       
        <article>
            <h3>Register as user</h3>
            <form method="POST"  action="Register">
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                    <li>First Name <input type="text" name="firstname"></li>
                    <li>Last Name <input type="text" name="lastname"></li>
                    <li>Email <input type="text" name="email"></li>
                    <li>Date of Birth <input type="text" id="datepicker" name="dob"</li>
                    <li>Sex 
                        <select name="item">
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="prefernototsay">Prefer not to say..</option>
                        </select>
                    </li>
                    
                    <li>Nationality</li><input type="text" id="country_selector">
                    <script>
                    $(function() {
                    $("#country").countrySelect();
                    });
                    </script>
                      
                   
                        
                        
                    <input type="submit" value="Submit">
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

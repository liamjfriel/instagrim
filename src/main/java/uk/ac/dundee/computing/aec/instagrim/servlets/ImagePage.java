package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import java.util.UUID;
import java.util.Date;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/Imagepage/*"
})
@MultipartConfig

public class ImagePage extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImagePage() {
        super();

    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        if(lg != null){
            if (lg.getlogedin()){
                String args[] = Convertors.SplitRequestPath(request); //Borrowed from Image, takes arguments of URL and splits it so we can get the username
                String picname = args[2];
                //UUID picuid is the converted picname from url
                UUID picuuid = UUID.fromString(picname);
                //New picture model called picmod
                PicModel picmod = new PicModel();
                //Set the cluster in that class
                picmod.setCluster(cluster);
                //Comment map  equals the one we get from picmodel
                List<Map> commentlist = picmod.getComments(picuuid);
                //Showpic pic object equals the return of the getmap method from picmod
                Pic showpic = picmod.getPic(Convertors.DISPLAY_PROCESSED, picuuid);
                //Set the attribute comments to the the comments we got from the picmodel
                request.setAttribute("comments", commentlist);
                //Set the attribute pic to the the picture we got from the picmodel
                request.setAttribute("pic", showpic);
                //Boolean canprofilepic 
                boolean canprofilepic = canBeProfilePicture(lg, showpic);
                //Set the attribute of canprofilepic to the return value of our function
                request.setAttribute("canprofilepic", canprofilepic);
                RequestDispatcher rd = request.getRequestDispatcher("/imagepage.jsp");
                rd.forward(request, response);
                } else {
                response.sendRedirect("/Instagrim/login.jsp");
            }
        } else {
            response.sendRedirect("/Instagrim/login.jsp");
        } 
              
    }

    //This returns true or false based off whether the uploader is also the logged in user
    private boolean canBeProfilePicture(LoggedIn lg, Pic pictocheck){
        //If the user that is logged in is the same person that uploaded the picture
        if(lg.getUsername().equals(pictocheck.getUploader()))
        {
            //Return true
            return true;
        }
        //Getting this far means that it's not the case, we could also do else here if we wanted
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        //If logged in
        if(lg != null){
            //If getloggedin returns true
            if (lg.getlogedin()){
                //Borrowed from Image, takes arguments of URL and splits it so we can get the username
                String args[] = Convertors.SplitRequestPath(request); 
                //String picname equa ls the end of the url              
                String picname= args[2];
                //If the user push the submit comment button
                if(request.getParameter("commentsubmit") != null){
                    //Set UUID to picstringid converted to UUID from string
                    UUID picid = UUID.fromString(picname);
                    //String comment equals the value of the comment text box
                    String comment=request.getParameter("commentext");
                    //String uploader equals the value of the hidden uploader field
                    String uploader=request.getParameter("uploader");
                    //New picture model called picmod
                    PicModel picmod = new PicModel();
                    //Set the cluster in that class
                    picmod.setCluster(cluster);
                    //Write the comment to the database, passing the picid, comment text and pic uploader
                    picmod.addComment(picid, lg.getUsername(), comment);
                    //Go back to the URL we came from
                    response.sendRedirect(request.toString());
                }
                //If the user clicked "makeprofile"
                if(request.getParameter("makeprofile") != null){
                    //Create new user object
                    User user = new User();
                    //Set the cluster in that class
                    user.setCluster(cluster);
                    //Set the usersprofile pic
                    user.setProfilePic(picname,lg.getUsername());
                    //Send redirect to usersprofle
                    response.sendRedirect("/Instagrim/profiles/" + lg.getUsername());
                }
                //If the user clicked "deletepic"
                if(request.getParameter("deletepic") != null){
                    //Get the date of the pic, this is the needed to delete it
                    Date datetopass = (Date) request.getAttribute("picdate");
                    //Create new user object
                    PicModel pm = new PicModel();
                    //Set the cluster in that class
                    pm.setCluster(cluster);
                    //Set UUID to picstringid converted to UUID from string
                    UUID picid = UUID.fromString(picname);
                    //Delete the picture from the database,passing the URL 
                    pm.deletePicture(picid,lg.getUsername());
                    //Send redirect to usersprofle
                    response.sendRedirect("/Instagrim/profiles/" + lg.getUsername());
                }

                } else {
                //Not logged in, send them to login page
                response.sendRedirect("/Instagrim/login.jsp");
            }
        } else {
            //Login object doesn't even exist, so not logged in, send them to login page
            response.sendRedirect("/Instagrim/login.jsp");
        } 
    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }
}

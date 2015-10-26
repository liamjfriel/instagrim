package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.utils.Bytes;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.UDTMapper;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayOutputStream;
import java.awt.image.ByteLookupTable;
import java.io.File;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.util.Collections;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.ColorConvertOp;
import java.awt.image.Kernel;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.UUID;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

public class PicModel {

    Cluster cluster;

    public void PicModel() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void insertPic(byte[] b, String type, String name, String user, int filter) {
        try {
            Convertors convertor = new Convertors();

            String types[]=Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid = convertor.getTimeUUID();
            
            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));

            output.write(b);
            byte []  thumbb = picresize(picid.toString(),types[1],filter);
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            byte[] processedb = null;
            //Switch before we process the image
            switch(filter){
                //If 1, that means black and white
                case 1: 
                {
                    //Decolour our picture
                    processedb = picapplyfilter(picid.toString(),types[1], 1);
                    //Break the switch
                    break;
                }
                //If 2, that means lighter
                case 2: 
                {
                    //Brightern our picture
                    processedb = picapplyfilter(picid.toString(),types[1], 2);
                    //Break the switch
                    break;
                }
                //If 3, that means darker
                case 3: 
                {
                    //Darken our picture
                    processedb = picapplyfilter(picid.toString(),types[1], 3);
                    //Break the switch
                    break;
                }
            }
            ByteBuffer processedbuf=ByteBuffer.wrap(processedb);
            int processedlength=processedb.length;
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);
            //New date object, Date added
            Date DateAdded = new Date();
            //Execute the following boundstatements
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            //Close the database connection
            session.close();

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
    }

    public byte[] picresize(String picid,String type,int filter) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage thumbnail = createThumbnail(BI,filter);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();
            
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    public byte[] picapplyfilter(String picid,String type,int filter) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI, filter);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage img, int filter) {
        //If the filter is 1 (grayscale)
        switch(filter){
            //1 grayscale
            case 1: 
            {
                //Scalr process the image, make it grayscale
                img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
                break;
            }
            //2 = normal
            case 2: 
            {
                /*
                //THE FOLLOWING WAS TAKEN FROM
                //WWW.JAVA2S.COM/Code/Java/2D-Graphics-GUI/ImageFilter.htm
                //I DO NOT TAKE CREDIT FOR THIS
                //This creates an array of float values
                byte[] invertArray = new byte[256];
                for(int i = 0; i < 256; i++){
                    invertArray[i] = (byte) (255 - i);
                }
                Forget it, cant get it to work anyway
                BufferedImageOp blurfilter = new LookupOp(new ByteLookupTable(0, invertArray),null);
                */
                //Scalr process the image, don't apply the antialiasing
                img = resize(img, Method.SPEED, 250, OP_ANTIALIAS);
                break;
            }
        }
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
   public static BufferedImage createProcessed(BufferedImage img, int filter) {
        //Int width equals the width of the image
        int Width=img.getWidth()-1;
        //If the filter is 1 (grayscale)
        switch(filter){
            //1 grayscale
            case 1: 
            {
                //Scalr process the image, make it grayscale
                img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
                break;
            }
            //2 = normal
            case 2: 
            {
                //Same thing, just don't apply the grayscale
                img = resize(img, Method.SPEED, Width, OP_ANTIALIAS);
                
                break;
            }
            
        }
        //Add a border to image
        return pad(img, 4);
    }
   
   //This method gets a list of the new photos added by the people they are following
    public List<Pic> getUserFeed(String username){
        //Declare list of lists of type Pic that we will use to store each users pictures
        List<Pic> feed = new LinkedList();
        //Declare list of pictures that will be stored in the above list and will store each users pic
        List<Pic> listofpicsforuser;
        //Create new user object
        User us = new User();
        //Set the cluster to cluster
        us.setCluster(cluster);
        //Get a set with all the followers of the user
        Set<String> following = us.followingSet(username);
        //If the list has nothing in it
        if(following == null){
            //Return null
            return null;
        }
        //So it isn't null, begin going through the set getting all the names
        Iterator<String> iterator;
        iterator = following.iterator();
        //While we've still got a list in front of us
        while (iterator.hasNext()) {
            //String picstoget equals one of the people the user is following
            String picstoget = (String) iterator.next();
            //Set listofpicsforuser equal to the return of their pic list
            listofpicsforuser = getPicsForUser(picstoget);
            //Iterate through this new list
            Iterator<Pic> piciterator = listofpicsforuser.iterator();
            while(piciterator.hasNext()){
                //Pic object that the iterator is on is referenced
                Pic userpic = (Pic) piciterator.next();
                //If the feed list is empty
                if(feed.isEmpty() == true){
                    //Add the picture to the feed list
                    feed.add(userpic);
                } else {
                    //Bool pic added 
                    boolean picadded = false;
                    //Iterator for the feed list intitialised
                    Iterator<Pic> feediterator = feed.iterator();
                    //Intialise counter that we will use to insert at certain indexes
                    int i = 0;
                    //Iterate through feed list, and tag it as feed loop
                    feedloop:
                    while(feediterator.hasNext()){
                       //Pic object that the iterator is on is referenced
                        Pic feedpic = (Pic) feediterator.next();
                        //If the pic from the users list is more recent than the one currently in the list
                        if(userpic.getUploaddate().after(feedpic.getUploaddate())){
                            //Add it to the feed, at the index the previous picture was at
                            feed.add(i, userpic);
                            //set picadded to true
                           picadded = true;
                            break feedloop;
                        }
                        //Increment the counter
                        i++;
                    }

                    //If the feed is over 15 objects, get rid of the extra ones
                    if(feed.size() > 14){
                        feed.subList(14, feed.size()).clear();
                    } else if (picadded == false) {
                        feed.add(userpic);
                    }
                }
              
            }
            
         
        }
        //Return a list of all the lists of pictures
        return feed;             
        
    }
   
    public LinkedList<Pic> getPicsForUser(String User) {
        //New linkedlist ob ject of type Pic
        LinkedList<Pic> Pics = new LinkedList();
        //Connect to the instagrim keyspace
        Session session = cluster.connect("instagrim");
        //Prepared statement that will select from userpics where login is provided in the boundstatement
        PreparedStatement ps = session.prepare("select picid,user,pic_added from userpiclist where user =?");
        //Result set is declared and set to null
        ResultSet rs = null;
        //New boundstatement object that will use the prepared statement
        BoundStatement boundStatement = new BoundStatement(ps);
        //Execute our boundstatement, and the return of it goes into the result set
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        //Bind the ? from the prepared statement to user
                        User));
        //If the result set is empty
        if (rs.isExhausted()) {
            //Print that no images were returned
            System.out.println("No Images returned");
            //Return null
            return null;
        //Otherwise
        } else {
            //Loop through the result set, row by row
            for (Row row : rs) {
                //Pic object pic declared
                Pic pic = new Pic();
                //UUID object created, set to the UUID value "picid" from the row
                java.util.UUID UUID = row.getUUID("picid");
                //Prints the UUID to the console, converting it to a string to do so
                System.out.println("UUID" + UUID.toString());
                //Set the UUID in the pic object to the one we just declared
                pic.setUUID(UUID);
                //Set the uploaddate to the pic_added value in the row
                pic.setUploaddate(row.getDate("pic_added"));
                //Set uploader to the "user" value of the row returned
                pic.setUploader(row.getString("user"));
                //Add the pic object to the Pics list we declared earlier
                Pics.add(pic);
            }
        }
        //Close the database connection
        session.close();
        //Return pics
        return Pics;
    }
    
    //Method for deleting the pictures
    public void deletePicture(UUID picid, String username){
        
        //New User object
        User us = new User();
        //Set the cluster
        us.setCluster(cluster);
        
        //If picid is the same as the users profile picture
        if(picid.equals(us.getProfilePic(username))){
            //Set the profile pic to null
            us.setProfilePic(null, username);
        }
        //Connect to the instagrim keyspace
        Session session = cluster.connect("instagrim");
        //DELETE FROM PIC TABLE
        //Prepare the delete statement
        PreparedStatement ps = session.prepare("delete from pics where picid=?");
        //Create new boundstatement object, passing the prepared statement
        BoundStatement boundStatement1 = new BoundStatement(ps);
        //Execute the boundstatement
        session.execute( // this is where the query is executed
                boundStatement1.bind( // here you are binding the 'boundStatement'
                        picid));
        //DELETE FROM userpiclist TABLE
        //Prepare the delete statement
        PreparedStatement qs = session.prepare("delete from userpiclist where user=? and picid = ?");
        //Create new boundstatement object, passing the prepared statement
        BoundStatement boundStatement2 = new BoundStatement(qs);
        //Execute the boundstatement
        session.execute( // this is where the query is executed
                boundStatement2.bind( // here you are binding the 'boundStatement'
                        username,picid));
        //Close connection to database
        session.close();
    }
    
    public void addComment(UUID picid, String username, String comment){
        //Connect to cluster instagrim
        Session session = cluster.connect("instagrim");
        //UDT mapper
        UDTMapper<PicComment> mapper = new MappingManager(session).udtMapper(PicComment.class);
        //Declare new object of class piccomment
        PicComment piccomment = new PicComment();
        //Set the fields in this class
        piccomment.setPicComment(username, comment);
        //Create a map with timestamp and piccomment
        Map<Date,UDTValue> commentfordatabase = new TreeMap(Collections.reverseOrder());
        //Get new date for this time
        Date datetoadd = new Date();
        //Add the timestamp and comment mapped to udt to our mapp
        commentfordatabase.put(datetoadd, mapper.toUDT(piccomment));
        //Result set set to null, this will be set properly in the switch below
        ResultSet rs = null; 
        //Preparedstatement object equals a prepared statement
        PreparedStatement ps = session.prepare("update pics set comments = comments + ? where picid=?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicoginate the values
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        
        //Execute our prepared statement
        session.execute( 
                boundStatement.bind( 
                    //Bind the variables commentmap and picid
                        commentfordatabase,picid));
        //Close connection to database
        session.close();
    }
    
    public List getComments(java.util.UUID picid)
    {
        //List of comments
        List<Map> commentlist = new LinkedList();
        //Connect to cluster instagrim
        Session session = cluster.connect("instagrim");
        //Result set set to null, this will be set properly in the switch below
        ResultSet rs = null; 
        //Preparedstatement object equals a prepared statement
        PreparedStatement ps = session.prepare("select comments from pics where picid = ?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        BoundStatement boundStatement = new BoundStatement(ps);
        //This is where the query is expected
        rs = session.execute(
            // here you are binding the 'boundStatement'
            boundStatement.bind(
            //Our passed picid
                picid));
        
        //If there's nothing in the resultset
        if (rs.isExhausted() || rs == null) { 
            //Print that there is no users found
            System.out.println("User not found."); 
            //Return null
            return null; 
        //Otherwise
        } else { 
            //For every row 
            for (Row row : rs) {
                //Map that we will add to the list
                Map<Date,PicComment> realmap = new TreeMap(Collections.reverseOrder());
                //UDT mapper
                UDTMapper<PicComment> mapper = new MappingManager(session).udtMapper(PicComment.class);
                //Set our tempcommentmap to the map in the row
                Map<Date,UDTValue> tempcommentmap = row.getMap("comments", Date.class, UDTValue.class);
                //Loop through every entry in the map (aka the one and only one) 
                for (Map.Entry<Date,UDTValue> entry : tempcommentmap.entrySet()){
                    //Set our new map that we will put in the list with piccomment type and not UDTValue
                    PicComment commentforlist = mapper.fromUDT(entry.getValue());
                    //Add the data retrieved to our map that will be added to the list
                    realmap.put(entry.getKey(), commentforlist);
                }
                
                //Add the commentmap to the list
                commentlist.add(realmap);
             }
            //Disconnect from database
            session.close();
            //Return the list
            return commentlist;
        }
       
    }
    //This methood returns a pic, using the picid
    public Pic getPic(int image_type, java.util.UUID picid) {
        //Connect to keyspace instagrim
        Session session = cluster.connect("instagrim");
        //Intialising values, set thenm to null
        ByteBuffer bImage = null;
        String type = null;
        String uploader = null;
        //Date upload date intialised as null
        Date uploaddate = null;
        //Map of comments is intialised as null
        Map<String,String> comments = null;
        //Int length intialised as 0
        int length = 0;
        try {
            //New convertors object converted created
            Convertors convertor = new Convertors();
            //New result set rs intiialised as null
            ResultSet rs = null;
            //New prepared statement ps set to null
            PreparedStatement ps = null;
            //If the parameter image_type equals 1
            if (image_type == Convertors.DISPLAY_IMAGE) {
                //Prepare the following statement that will get the following values from the databse
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            //If it's 2
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                //Same thing, get the thumbnail and relevant information
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            //If it's three
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                //You get the idea
                ps = session.prepare("select processed,processedlength,type,interaction_time,user from pics where picid =?");
            }
            //New boundstatement object created, with the prepared statement passed for binding
            BoundStatement boundStatement = new BoundStatement(ps);
            //Result set is the return of the query we are binding and executing
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));
            //If the result set is empty
            if (rs.isExhausted()) {
                //Then there's no images with that picid
                System.out.println("No Images returned");
                //So retur null
                return null;
            //Otherwise
            } else {
                //Iterate through each row in the result set
                for (Row row : rs) {
                    //If the image type equals 1
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        //Set bImage to the bytes stored in "image" in the row
                        bImage = row.getBytes("image");
                        //Set length to the interger stored in "imagelength" in the row
                        length = row.getInt("imagelength");
                        /*
                        //And upload date
                        uploaddate = row.getDate("interaction_time");
                        //And user that uploaded the pic
                        uploader = row.getString("user");
                        */
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        //Thumbnail pic bytes
                        bImage = row.getBytes("thumb");
                        //Length int value in the row
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        //Bytes that are in "processed" field
                        bImage = row.getBytes("processed");
                        //Length that is the int value in the row
                        length = row.getInt("processedlength");
                        //Date is the date value "interaction_time" in the row
                        uploaddate = row.getDate("interaction_time");
                        //And uploader is string user in row 
                        uploader = row.getString("user");
                    }
                    //Type is set to the string value "type" from our row
                    type = row.getString("type");

                }
            }
        //If an exception happens
        } catch (Exception et) {
            //Print that we couldn't get a pic, with the exception
            System.out.println("Can't get Pic" + et);
            //Returnnull
            return null;
        }
        //Close the session
        session.close();
        //Create new Pic object called p
        Pic p = new Pic();
        //Set all the relevant fields in Picture class
        p.setPic(bImage, length, type, uploaddate, picid, uploader);
        //Return p
        return p;

    }

}

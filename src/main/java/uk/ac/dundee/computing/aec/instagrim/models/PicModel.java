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
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;
import java.util.Map;
import java.util.HashMap;
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

    public void insertPic(byte[] b, String type, String name, String user) {
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
            byte []  thumbb = picresize(picid.toString(),types[1]);
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            byte[] processedb = picdecolour(picid.toString(),types[1]);
            ByteBuffer processedbuf=ByteBuffer.wrap(processedb);
            int processedlength=processedb.length;
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
    }

    public byte[] picresize(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage thumbnail = createThumbnail(BI);
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
    
    public byte[] picdecolour(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI);
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

    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
   public static BufferedImage createProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
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
                            break feedloop;
                        }
                        //Increment the counter
                        i++;
                    }

                    //If the feed is over 15 objects, get rid of the extra ones
                    if(feed.size() > 14){
                        feed.subList(14, feed.size()).clear();
                    }
                }
              
            }
            
         
        }
        //Return a list of all the lists of pictures
        return feed;             
        
    }
   
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select picid,user,pic_added from userpiclist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                //Set the uploaddate to the pic_added value
                pic.setUploaddate(row.getDate("pic_added"));
                //Set uploader to the "user" value of the row returned
                pic.setUploader(row.getString("user"));
                Pics.add(pic);
            }
        }
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
        Map<Date,UDTValue> commentfordatabase = new HashMap();
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
                Map<Date,PicComment> realmap = new HashMap();
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
            //Return the list
            return commentlist;
        }
       
    }

    public Pic getPic(int image_type, java.util.UUID picid) {
        Session session = cluster.connect("instagrim");
        ByteBuffer bImage = null;
        String type = null;
        String uploader = null;
        Date uploaddate = null;
        Map<String,String> comments = null;
        int length = 0;
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if (image_type == Convertors.DISPLAY_IMAGE) {
                
                ps = session.prepare("select image,imagelength,type,interaction_time,user from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                        uploaddate = row.getDate("interaction_time");
                        uploader = row.getString("user");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
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

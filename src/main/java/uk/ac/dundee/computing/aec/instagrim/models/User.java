/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.UDTMapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.*;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.UDTValue;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import java.lang.Object;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;
import uk.ac.dundee.computing.aec.instagrim.lib.Addresses;
import java.lang.Object;
import java.util.Set;
import java.util.HashSet;
/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    public User(){
        
    }
    //Test
    
    public boolean RegisterUser(String username, String Password, String firstname, String lastname, String email, String sex, String dob, String streetname, String city, String zip, String country){
        
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        //Use instagrim keyspace
        Session session = cluster.connect("instagrim");
        
       // Syntax wise, this should work. However for some reason it does not. 
       // PreparedStatement ps = session.prepare("insert into userprofiles (login,password,first_name,last_name,email,sex,addresses) Values (?,?,?,?,?,?,{'home':{street:?,city:?,zip:?,country:?}})")
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password,first_name,last_name,email,sex,dob,addresses) Values(?,?,?,?,?,?,?,?) IF NOT EXISTS");
       //Address object created, set all the fields in it
        Addresses useraddress = new Addresses();
        //Set the address object with all the passed data 
        useraddress.setAddresses(streetname, city, zip, country);
        //Create udtmapper class
        UDTMapper<Addresses> mapper = new MappingManager(session).udtMapper(Addresses.class);
        //Hashmap object of type String and UDT value created
        Map<String,UDTValue> addressmap = new HashMap();
        //Put key "home" and the address class converted to the relevant UDT into the map
        addressmap.put("home",mapper.toUDT(useraddress));
        //New object of boundstatment
        BoundStatement boundStatement = new BoundStatement(ps);
        //Execute our boundstatement
        session.execute(
                //Bind the values
                boundStatement.bind( // here you are binding the 'boundStatement'
                   //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                        username,EncodedPassword,firstname, lastname, email, sex,dob,addressmap));
        //We are assuming this always works.  Also a transaction would be good here !
        //Close the session
       session.close();
        return true;
    }
    
    
    public void updateUser(String username, String password, String firstname, String lastname, String email, String sex, String dob, String streetname, String city, String zip, String country)
    {
        //Connect to cluster instagrim
        Session session = cluster.connect("instagrim");
        //Address object created, set all the fields in it
        Addresses useraddress = new Addresses();
        //Set the address object with all the passed data 
        useraddress.setAddresses(streetname, city, zip, country);
        //New object that will be used to encrpyt password with SHA1
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        //String encoded password which will be used as the output of the SHA1 encryption
        String EncodedPassword=null;
        //Create udtmapper class
        UDTMapper<Addresses> mapper = new MappingManager(session).udtMapper(Addresses.class);
        //Hashmap object of type String and UDT value created
        Map<String,UDTValue> addressmap = new HashMap();
        //Put key "home" and the address class converted to the relevant UDT into the map
        addressmap.put("home",mapper.toUDT(useraddress));;
        //Try
        try {
            //Encode the password to SHA1
            EncodedPassword= sha1handler.SHA1(password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
        }
        //Create prepared statement that will be used to update
        PreparedStatement ps = session.prepare("update userprofiles set password = ?, first_name = ?, last_name = ?, email = ?, sex = ?, dob = ?, addresses = ? where login=?"); 
        //As casandra does not duplicate the values
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        //Execute bound statement
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                   //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                        password,firstname,lastname,email,sex,dob,addressmap,username));
        //Close the session
       session.close();
    }
    
    public void followUser(String follower, String followtarget)
    {
        
        Session session = cluster.connect("instagrim");
        
        Set<String> set = new HashSet();
        set.add(follower);
        PreparedStatement ps = session.prepare("update userprofiles set followers = followers + ? where login=?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        //Execute bound statement
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                   //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                        set,followtarget));
        
        //Now add the following value from the person who was following this user
        ps = session.prepare("update userprofiles set following = following + ? where login=?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        boundStatement = new BoundStatement(ps); // Create new boundstatement object
        //New object of type set
        Set<String> followingset = new HashSet();
        //Add followtarget to this set
        followingset.add(followtarget);
        //Execute the CQL command
        session.execute( 
                boundStatement.bind(
                    //Bind followingset and follower variables
                        followingset,follower));
        //Close the session
        session.close();
    }
    
    
    public boolean isFollowing(String follower, String followtarget)
    {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select followers from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        followtarget));
        //Close the session
        session.close();
        if (rs.isExhausted()) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return false; //Return null
            
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               Set<String> set = row.getSet("followers", String.class);
               for (String isfollower : set){ //For every item in the set
                   //If the follower is already in the set
                   if(isfollower.equals(follower)){
                       return true; //Return true
                   }
               }
              
               return false; //If we get here, the user is not in the follower set
            }
          
        }
     
     return false;   
    }
    
    

    
    public List<Map> userSearch(String searchby,String tosearch)
    {
        
        Map<String,String> searchresults = new HashMap(); 
        List<Map> listofresults = new ArrayList();
        Session session = cluster.connect("instagrim");
        ResultSet rs = null; //Result set set to null, this will be set properly in the switch below
        
        PreparedStatement ps = session.prepare("select login,description,sex,profilepictureid from userprofiles where login =?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
            boundStatement.bind( // here you are binding the 'boundStatement'
            //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                tosearch));
        //Close the session
        session.close();
        if (rs.isExhausted() || rs == null) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return null; //Return null
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               searchresults.put("login", row.getString("login"));
               searchresults.put("sex", row.getString("sex"));
               searchresults.put("description",row.getString("description"));
               searchresults.put("profilepicid", row.getString("profilepictureid"));
               listofresults.add(searchresults);
             }
         
            return listofresults;
        }
        
       
    }
    
    public void unfollowUser(String follower, String followtarget)
    {
        
        Session session = cluster.connect("instagrim");
        
        Set<String> followerset = new HashSet();
        followerset.add(follower);
        PreparedStatement ps = session.prepare("update userprofiles set followers = followers - ? where login=?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        //Execute the CQL preprared statement we just made
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                   //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                        followerset,followtarget));
        
        //Now remove the following value from the person who was following this user
        ps = session.prepare("update userprofiles set following = following - ? where login=?"); //Because this is a set, we don't really have to worry if the record already exists in there
        //As casandra does not duplicate the values
        boundStatement = new BoundStatement(ps); // Create new boundstatement object
        //New object of type set
        Set<String> followingset = new HashSet();
        //Add followtarget to this set
        followingset.add(followtarget);
        //Execute the CQL command
        session.execute( 
                boundStatement.bind(
                    //Bind followingset and follower variables
                        followingset,follower));
        
         //Close the session
        session.close();
        
    }
    
    //Return a set with all following users
    public Set<String> followingSet(String username){
        //Connect to cluster instagrim
        Session session = cluster.connect("instagrim");
        //New prepared statement, that will select following set where the login equals a bound parameter
        PreparedStatement ps = session.prepare("select following from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        //Close the session
        session.close();
        if (rs.isExhausted()) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return null; //Return null
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               Set<String> set = row.getSet("following", String.class);
               return set;
             }
        }
        return null;
    }
    
    //Return a set with all followers
    public Set<String> followerSet(String username){
    
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select followers from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        //Close the session
        session.close();
        if (rs.isExhausted()) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return null; //Return null
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               Set<String> set = row.getSet("followers", String.class);
                //Close the session
                session.close();
               return set;
             }
        }
        //Close the session
        session.close();
        return null;
    }
    
    /* Returns a list with all the publicly viewable user data*/
    public Map UserInfoMap(String username)
    {
        
        Map<String,String> actualuserinformation = new HashMap(); 
        
        Session session = cluster.connect("instagrim");
                //Declare the hashmap we will return
         //Start new session and connect to cluster
        
        PreparedStatement ps = session.prepare("select first_name,last_name,sex,dob,addresses,followers,profilepictureid,email from userprofiles where login =?");
        ResultSet rs = null; //Declare a new result set object
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        //Close the session
         session.close();
        //Okay, so we borrowed this from the method IsValidUser
        if (rs.isExhausted()) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return null; //Return null
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               UDTMapper<Addresses> mapper = new MappingManager(session).udtMapper(Addresses.class);
               Map<String,UDTValue> addresses = row.getMap("addresses", String.class, UDTValue.class);
               Set<String> set = new HashSet(); 
               set = row.getSet("followers", String.class);
               
               for(String key : addresses.keySet()) {
                   Addresses address = mapper.fromUDT(addresses.get(key));
                   actualuserinformation.put("Street", address.getStreet()); 
                   actualuserinformation.put("Town", address.getCity()); //Put
                   actualuserinformation.put("Country", address.getCountry());
                   actualuserinformation.put("Zip", address.getZip());
               }
                //Put the following strings from the row into the map
                actualuserinformation.put("FirstName", row.getString(0));
                actualuserinformation.put("SecondName", row.getString(1));
                actualuserinformation.put("Sex", row.getString(2));
                actualuserinformation.put("DOB", row.getString(3));
                actualuserinformation.put("ProfilePic", row.getString(6));
                actualuserinformation.put("Email", row.getString(7));
                
                return actualuserinformation;
            }
        }
        
        return actualuserinformation; //Return our map now that it's full of data
    }
    
    public boolean IsValidUser(String username, String Password){
        
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        //Close the session
         session.close();
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                //Close the session
                session.close();
                if (StoredPass.compareTo(EncodedPassword) == 0)
                    return true;
            }
        }
    return false;  
    }
    
    public void setProfilePic(String pic, String user){
        //Connect to the keyspace instagrim
        Session session = cluster.connect("instagrim");
        //Create prepared statement where we enter the profilepic id to the user
        PreparedStatement ps = session.prepare("update userprofiles set profilepictureid = ? where login=?");
        //Create boundstatement
        BoundStatement boundStatement = new BoundStatement(ps);
        //Execute our boundstatement
        session.execute(
                boundStatement.bind(
                        pic, user));
         //Close the session
        session.close();
        
    }
    
    public UUID getProfilePic(String username){
        //Connect to the keyspace instagrim
        Session session = cluster.connect("instagrim");
        //Create prepared statement where we enter the profilepic id to the user
        PreparedStatement ps = session.prepare("select profilepictureid from userprofiles where login=?");
        //Result set intialised as null
        ResultSet rs = null;
        //Create boundstatement
        BoundStatement boundStatement = new BoundStatement(ps);
        //Execute our boundstatement
        rs = session.execute(
                boundStatement.bind(
                        username));
        //Close the session
         session.close();
        //In the row we got back in the result set
        for (Row row : rs) {
            //Set the string to the profilepicture id
            String picid = row.getString("profilepictureid");
            //If the picid equals null
            if(picid == null){
                //Return null
                return null;
            }
            //Return the picid as a UUID 
            return UUID.fromString(picid);
        }
        return null;
    }
    
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }       

    
}

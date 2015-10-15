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
import java.util.HashMap;
import java.util.Iterator;
import uk.ac.dundee.computing.aec.instagrim.lib.Addresses;
import java.lang.Object;
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
        Session session = cluster.connect("instagrim");
       // Syntax wise, this should work. However for some reason it does not. 
       // PreparedStatement ps = session.prepare("insert into userprofiles (login,password,first_name,last_name,email,sex,addresses) Values (?,?,?,?,?,?,{'home':{street:?,city:?,zip:?,country:?}})");
        //So instead, we'll do this which probably allows for MYSQL injection. This will be fixed eventually.
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password,first_name,last_name,email,sex,dob,addresses) Values(?,?,?,?,?,?,?,{'home':{street:'"+streetname+"',city:'"+city+"',zip:'"+zip+"',country:'"+country+"'}}) IF NOT EXISTS");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                   //     username,EncodedPassword,firstname,lastname,email,sex,streetname,city,zip,country));
                        username,EncodedPassword,firstname, lastname, email, sex,dob));
        //We are assuming this always works.  Also a transaction would be good here !
        /*
        String cqlQuery = ("insert into userprofiles (login,addresses) values ('"+username+"',{'home':{street:'"+streetname+"',city:'"+city+"',zip:'"+zip+"',country:'"+country+"'}}) IF NOT EXISTS");
        
        PreparedStatement ns = session.prepare(cqlQuery);
        BoundStatement addAddress = new BoundStatement(ns);
        session.execute(addAddress);
        
       */ 
        return true;
    }
    
    /* Returns a list with all the publicly viewable user data*/
    public Map UserInfoMap(String username)
    {
        Row userinformationbeforeparse; //Declare our list of type row
        
        Map<String,String> actualuserinformation = new HashMap(); 
        
        Session session = cluster.connect("instagrim");
                //Declare the hashmap we will return
         //Start new session and connect to cluster
        
        PreparedStatement ps = session.prepare("select first_name,last_name,sex,dob,addresses from userprofiles where login =?");
        ResultSet rs = null; //Declare a new result set object
        BoundStatement boundStatement = new BoundStatement(ps); // Create new boundstatement object
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        
        //Okay, so we borrowed this from the method IsValidUser
        if (rs.isExhausted()) { //If there's nothing in the resultset
            System.out.println("User not found."); //Print that there is no users found
            return null; //Return null
        } else { //Otherwise
            for (Row row : rs) {
               //THIS WAS ALL SOURCED FROM THE DOCUMENTATION FOR CASSANDRA
               UDTMapper<Addresses> mapper = new MappingManager(session).udtMapper(Addresses.class);
               Map<String,UDTValue> addresses = row.getMap("addresses", String.class, UDTValue.class);
               for(String key : addresses.keySet()) {
                   Addresses address = mapper.fromUDT(addresses.get(key));
                   actualuserinformation.put("Town", address.getCity()); //Put
                   actualuserinformation.put("Country", address.getCountry());
               }
              //  Addresses addresses = mappingsession.getFromRow(Addresses.class, row);
                
              //  addresses = mappingsession.get(Addresses.class,4);
                
                actualuserinformation.put("FirstName", row.getString(0));
                actualuserinformation.put("SecondName", row.getString(1));
                actualuserinformation.put("Sex", row.getString(2));
                actualuserinformation.put("DOB", row.getString(3));
                
                return actualuserinformation;
            }
        }
        
        
        
        /*
        userinformationbeforeparse = rs.one(); //Set our userinformationbeforeparse list to the result
        if(rs == null){
            actualuserinformation.put("FirstName","test");
        } else {
            //What we are doing here is parsing the userinformationbeforeparse row that contains the result of our query and pulling information that we want from it and 
            //..putting it into our hashmap that we will pass
            actualuserinformation.put("FirstName", userinformationbeforeparse.getString(0));
            actualuserinformation.put("SecondName", userinformationbeforeparse.getString(1));
            actualuserinformation.put("Sex", userinformationbeforeparse.getString(2));
            actualuserinformation.put("DOB", userinformationbeforeparse.getString(3));
            //Next three lines get the town/city and  country and put them in the hashmap. They do this by pulling the map from the row, then pulling data from keys in that map
           // actualuserinformation.put("Town", userinformationbeforeparse.getMap("addresses",String.class,String.class).get("city"));
           // actualuserinformation.put("Country", userinformationbeforeparse.getMap("addresses",String.class,String.class).get("country"));   
        }
        */
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
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0)
                    return true;
            }
        }
   
    
    return false;  
    }
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}

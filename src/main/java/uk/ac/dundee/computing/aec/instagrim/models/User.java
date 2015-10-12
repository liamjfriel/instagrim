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
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.lib;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import static com.datastax.driver.core.DataType.Name.UDT;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.UDTMapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.*;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.annotations.UDT;
/**
 *
 * @author liam
 */
@UDT (keyspace = "instagrim", name = "address")
public class Addresses {
        
        
        private String street;
        private String city;
        private String zip;
        private String country;
        
        public Addresses(){
        
        }
        
        public String getStreet()
        {
            return street;
        }
        
        public void setStreet(String street)
        {
            this.street = street;
        }
        
        public String getCity()
        {
            return city;
        }
        
        public void setCity(String city)
        {
            this.city = city;
        }
        
        public String getZip()
        {
            return zip;
        }
        
        public void setZip(String zip)
        {
            this.zip = zip;
        }
        
        public String getCountry()
        {
            return country;
        }
        
        public void setCountry(String country)
        {
            this.country = country;
        }
    
}

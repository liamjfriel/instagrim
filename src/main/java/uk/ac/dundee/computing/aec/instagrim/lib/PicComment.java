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
@UDT (keyspace = "instagrim", name = "piccomment")
public class PicComment {
        
        
        private String author;
        private String comment;
        
        public PicComment(){
        
        }
        
        public void setPicComment(String author, String comment)
        {
            this.author = author;
            this.comment = comment;
        }
        public String getAuthor()
        {
            return author;
        }
        
        public void setAuthor(String author)
        {
            this.author = author;
        }
        
        public String getComment()
        {
            return comment;
        }
        
        public void setComment(String comment)
        {
            this.comment = comment;
        }
        
    
}

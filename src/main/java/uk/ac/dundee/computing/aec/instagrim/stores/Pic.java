/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Administrator
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private UUID UUID=null;
    private String uploader;
    private Date uploaddate;
    
    public void Pic() {

    }
    public void setUUID(java.util.UUID UUID){
        this.UUID =UUID;
    }
    public String getSUUID(){
        return UUID.toString();
    }
    public void setPic(ByteBuffer bImage, int length,String type, Date uploaddate, UUID picid, String uploader) {
        this.bImage = bImage;
        this.length = length;
        this.type=type;
        this.uploaddate = uploaddate;
        this.UUID = picid;
        this.uploader = uploader;
    }

    public ByteBuffer getBuffer() {
        return bImage;
    }

    public int getLength() {
        return length;
    }
    
    public String getType(){
        return type;
    }

    public byte[] getBytes() {
         
        byte image[] = Bytes.getArray(bImage);
        return image;
    }
    
    public String getUploader() {
        return uploader;
    }
    
    public void setUploader(String uploader){
        this.uploader = uploader;
    }
            
    public Date getUploaddate() {
        return uploaddate;
    }
    
    public void setUploaddate(Date uploaddate){
        this.uploaddate = uploaddate;
    }
    
    /*
    public void setComments(Map comments){
        this.comments = comments;
    }
    
    public Map getComments(){
        return comments;
    }

    */
}

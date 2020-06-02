package pers.lee.common.lang.file;

import java.io.File;
import java.util.Date;

/**
 * FileInfo
 *
 * @author Drizzt Yang
 */
public class FileInfo {
    private String name;
    private Long size;
    private Date lastModifyDate;

    public FileInfo(File file) {
        name = file.getName();
        size = file.getTotalSpace();
        lastModifyDate = new Date(file.lastModified());
    }

    public FileInfo(String name, Long size, Date lastModifyDate) {
    	this.name = name;
    	this.size = size;
    	this.lastModifyDate = lastModifyDate;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }
}

package pers.lee.common.lang.file;

import java.io.File;

/**
 * DirectoryInfo
 *
 * @author Drizzt Yang
 */
public class DirectoryInfo {
    private String name;
    private int files = 0;

    public DirectoryInfo(File file) {
        name = file.getName();
        String[] list = file.list();
        if(list != null) {
            files = list.length;
        }
    }
    
    public DirectoryInfo(String name, int files){
    	this.name = name;
    	this.files = files;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }
}

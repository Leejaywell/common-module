package pers.lee.common.lang.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FileService
 *
 * @author Drizzt Yang
 * @since 12-1-30 上午10:33
 */
public class FileService implements IFileService {
    private String baseDirectory;

    public FileService() {
    }

    public FileService(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public List<DirectoryInfo> listDirectories(String subDirectory) {
        File parentDirectory;
        if(subDirectory == null) {
            parentDirectory = new File(baseDirectory);
        } else {
            parentDirectory = new File(baseDirectory + File.separator + subDirectory);
        }
        List<DirectoryInfo> directories = new ArrayList<DirectoryInfo>();
        for(File file : parentDirectory.listFiles()) {
            if(file.isDirectory()) {
                directories.add(new DirectoryInfo(file));
            }
        }
        return directories;
    }

    @Override
    public List<FileInfo> listFiles(String subDirectory) {
        File parentDirectory;
        if(subDirectory == null) {
            parentDirectory = new File(baseDirectory);
        } else {
            parentDirectory = new File(baseDirectory + File.separator + subDirectory);
        }
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        for(File file : parentDirectory.listFiles()) {
            if(file.isFile()) {
                fileInfos.add(new FileInfo(file));
            }
        }
        return fileInfos;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
}

package pers.lee.common.lang.file;

import java.util.List;

/**
 * IFileService
 *
 * @author Drizzt Yang
 */
public interface IFileService {
    List<DirectoryInfo> listDirectories(String subDirectory);

    List<FileInfo> listFiles(String subDirectory);
}

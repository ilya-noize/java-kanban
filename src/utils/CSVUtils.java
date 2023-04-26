package utils;

import exception.ManagerException;

import java.io.File;
import java.io.IOException;

public class CSVUtils {
    File file;

    public CSVUtils(String fileName){
        File file = new File(fileName);
        if(isExists(file)) {
            this.file = file;
        }
    }

    private boolean isExists(File file){
        if(!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                throw new ManagerException("Невозможно создать файл " + file.getName());
            }
        }
        return false;
    }

    public File getFile() {
        return file;
    }
}

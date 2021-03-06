package deploy.tectuality.com;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;


public class Zip {

	private String rootFolder;
	
	/*
     * Zip function zip all files and folders
     */
    @SuppressWarnings("finally")
    public boolean zipFiles(String srcFolder, String destZipFile) {
        boolean result = false;
        this.rootFolder = srcFolder;
        try {
            System.out.println("Program Start zipping the given files");
            /*
             * send to the zip procedure
             */
            zipFolder(srcFolder, destZipFile);
            result = true;
            System.out.println("Given files are successfully zipped to " + destZipFile);
        } catch (Exception e) {
            System.out.println("Some Errors happned during the zip process");
        } finally {
            return result;
        }
    }

    /*
     * zip the folders
     */
    private void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        /*
         * create the output stream to zip file result
         */
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        /*
         * add the folder to the zip
         */
        addFolderToZip("", srcFolder, zip, true);
        /*
         * close the zip objects
         */
        zip.flush();
        zip.close();
    }

    /*
     * recursively add files to the zip files
     */
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag, boolean firstCall) throws Exception {
        /*
         * create the file object for inputs
         */
        File folder = new File(srcFile);
        /*
         * if the folder is empty add empty folder to the Zip file
         */
        if (flag == true) {
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
        } else if(!folder.isHidden() || folder.getName().equalsIgnoreCase(".htaccess")){ /*
                 * if the current name is directory, recursively traverse it
                 * to get the files
                 */
            if (folder.isDirectory()) {
                /*
                 * if folder is not empty
                 */
                addFolderToZip(path, srcFile, zip, false);
            } else {
                /*
                 * write the file to the output
                 */
            	if(firstCall){
            		return;
            	}
            	
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    /*
                     * Write the Result
                     */
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    /*
     * add folder to the zip file
     */
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, boolean firstCall) throws Exception {
        File folder = new File(srcFolder);

        /*
         * check the empty folder
         */
        if (folder.list().length == 0) {
        	addFileToZip(path, srcFolder, zip, true, firstCall);
        } else {
            /*
             * list the files in the folder
             */
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false, firstCall);
                } else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false, firstCall);
                }
            }
        }
    }
}

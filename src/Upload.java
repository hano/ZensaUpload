import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;


public class Upload {

	public static void main(String[] args) throws Exception {	
		String command = args[0];
		String path = args[1];
		
		if(command.equalsIgnoreCase("upload")){
			String zipName = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date()) + ".zip";
			String zipPath = FileUtils.getTempDirectoryPath() + zipName;
			
			Delete del = new Delete(path);
			del.delete();

			Zip zip = new Zip();
			zip.zipFiles(path, zipPath);

			try{
				DropBoxWrapper dropBox = new DropBoxWrapper();
		        dropBox.auth();
				dropBox.upload(zipPath, zipName);	
			} finally {
				if(FileUtils.deleteQuietly(new File(zipPath))){
					System.out.println("Removed " + zipPath);
				} else {
					System.out.println("Please remove the zip yourself " + zipPath);
				}
			}
		} else if(command.equalsIgnoreCase("delete")){
			DropBoxWrapper dropBox = new DropBoxWrapper();
	        dropBox.auth();
	        String _path = path;
	        if(path.startsWith("http")){
	        	String[] url = path.split("/");
		        _path = "/public/" + url[url.length - 1];	
	        }
	        if(dropBox.delete(_path)){
	        	System.out.println("Successful deleted " + _path);	
	        } else {
	        	System.out.println("Error deleting " + _path);
	        }
		} else {
			System.out.println("Command " + command + " not found. Try: upload or delete");
		}
		
	}

}

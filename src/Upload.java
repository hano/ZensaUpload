import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;


public class Upload {

	public static void main(String[] args) throws Exception {		
		String zipName = new SimpleDateFormat("yyyy-MM-dd-HH_mm").format(new Date()) + ".zip";
		String zipPath = FileUtils.getTempDirectoryPath() + zipName;
		
		Delete del = new Delete(args[0]);
		del.delete();

		Zip zip = new Zip();
		zip.zipFiles(args[0], zipPath);

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
	}

}

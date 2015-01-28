import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Delete {

	String rootPath;

	public Delete(String rootPath) {
		super();
		this.rootPath = rootPath;
	}

	public void delete() throws Exception {
		ArrayList<String> folders = new ArrayList<String>();
		folders.add(this.rootPath);
		folders.add(this.rootPath + "/templates");
		folders.add(this.rootPath + "/templates_c");
		folders.add(this.rootPath + "/templates_c_portal");

		ArrayList<File> filesToDelete = new ArrayList<File>();

		for (String folder : folders) {
			File f = new File(folder);
			if (f.exists() && f.isDirectory()) {
				System.out.println(f + " exists");
				filesToDelete.add(f);
			} else {
				throw new Exception(f + " is not a directory");
			}
		}
		filesToDelete.remove(0);

		for (File f : filesToDelete) {
			this._delete(f);
			f.mkdir();
		}
	}

	private void _delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				this._delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

}

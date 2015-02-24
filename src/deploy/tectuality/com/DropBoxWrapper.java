package deploy.tectuality.com;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

public class DropBoxWrapper {

	private String APP_KEY = "";
	private String APP_SECRET = "";
	private String code;
	private DbxAppInfo appInfo;
	private DbxRequestConfig config;
	private DbxWebAuthNoRedirect webAuth;
	private DbxClient client;
	private String accessToken;
	private File tokenFile = new File("token.txt");
	private File urlFile = new File("url.txt");
	private File configFile = new File("deploy_config.txt");

	public DropBoxWrapper() {
		this.setCode("");
		this.config = new DbxRequestConfig("uploadPM/1.0", Locale.getDefault()
				.toString());
		try {
			this.APP_KEY = FileUtils.readFileToString(configFile).split("\n")[0];
			this.APP_SECRET = FileUtils.readFileToString(configFile).split("\n")[1];
			this.accessToken = FileUtils.readFileToString(tokenFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		this.webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	}

	public void auth() throws IOException, DbxException {
		if (!this.getAccessToken().equalsIgnoreCase("")) {
			this.finishAuth();
			return;
		}
		String authorizeUrl = webAuth.start();
		System.out.println("1. Go to: " + authorizeUrl);
		System.out
				.println("2. Click \"Allow\" (you might have to log in first)");
		System.out.println("3. Copy the authorization code.");
		this.setCode(new BufferedReader(new InputStreamReader(System.in))
				.readLine().trim());
		// System.out.println(code);
		DbxAuthFinish authFinish = this.webAuth.finish(code);
		this.setAccessToken(authFinish.accessToken);
		this.finishAuth();
	}

	private void finishAuth() throws DbxException, IOException {
		this.finishAuth(this.getAccessToken());
	}

	private void finishAuth(String accessToken) throws DbxException,
			IOException {
		// TODO Auto-generated method stub
		try {
			this.client = new DbxClient(config, accessToken);
			System.out.println("Linked account: "
					+ this.client.getAccountInfo().displayName);
		} catch (DbxException e) {
			this.setAccessToken("");
			this.auth();
		}
	}

	public void upload(String filePath, String fileName) throws IOException,
			DbxException {
		File inputFile = new File(filePath);
		FileInputStream inputStream = new FileInputStream(inputFile);
		try {
			DbxEntry.File uploadedFile = this.client.uploadFile("/public/"
					+ fileName, DbxWriteMode.add(), inputFile.length(),
					inputStream);

			String publicURL = "https://dl.dropboxusercontent.com/u/"
					+ this.client.getAccountInfo().userId + "/"
					+ uploadedFile.name;
			//StringSelection stringSelection = new StringSelection(publicURL);
			//Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			//clpbrd.setContents(stringSelection, null);
			try {
				FileUtils.writeStringToFile(this.urlFile, publicURL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out
					.println("Uploaded and copied to clipboard: " + publicURL);
		} finally {
			inputStream.close();
		}
	}
	
	public boolean delete(String path){
		try {
			this.client.delete(path);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	String getShareURL(String strURL) {
		URLConnection conn = null;
		String redirectedUrl = null;
		try {
			URL inputURL = new URL(strURL);
			conn = inputURL.openConnection();
			conn.connect();

			InputStream is = conn.getInputStream();
			System.out.println("Redirected URL: " + conn.getURL());
			redirectedUrl = conn.getURL().toString();
			is.close();

		} catch (Exception e) {
		}

		return redirectedUrl;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private String getCode() {
		return this.code;
	}

	public void setAccessToken(String accessToken) {
		System.out.println(accessToken);
		this.accessToken = accessToken;
		try {
			FileUtils.writeStringToFile(this.tokenFile, accessToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getAccessToken() {
		return this.accessToken;
	}
}

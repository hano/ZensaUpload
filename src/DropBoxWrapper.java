import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private final String APP_KEY = "";
	private final String APP_SECRET = "";
    private String code;
    private DbxAppInfo appInfo;
    private DbxRequestConfig config;
    private DbxWebAuthNoRedirect webAuth;
    private DbxClient client;
    private String accessToken;
    private File tokenFile = new File("token.txt");
    
    public DropBoxWrapper(){
    	this.setCode("");
    	this.appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    	this.config = new DbxRequestConfig(
                "ZensaUpload/1.0", Locale.getDefault().toString());
        this.webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		try {
			this.accessToken = FileUtils.readFileToString(tokenFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void auth() throws IOException, DbxException{
    	if(!this.getAccessToken().equalsIgnoreCase("")){
    		this.finishAuth();
    		return;
    	}
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        this.setCode(new BufferedReader(new InputStreamReader(System.in)).readLine().trim());
        //System.out.println(code);
        DbxAuthFinish authFinish = this.webAuth.finish(code);
		this.setAccessToken(authFinish.accessToken);
        this.finishAuth();
    }
    
    private void finishAuth() throws DbxException, IOException {
    	this.finishAuth(this.getAccessToken());
    }

	private void finishAuth(String accessToken) throws DbxException, IOException {
		// TODO Auto-generated method stub
		try{
	        this.client = new DbxClient(config, accessToken);
	        System.out.println("Linked account: " + this.client.getAccountInfo().displayName);
		} catch (DbxException e){
			this.setAccessToken("");
			this.auth();
		}
	}
	
	public void upload(String filePath, String fileName) throws IOException, DbxException{
		File inputFile = new File(filePath);
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = this.client.uploadFile("/Public/site/" + fileName, DbxWriteMode.add(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	private String getCode(){
		return this.code;
	}
	
	public void setAccessToken(String accessToken){
		System.out.println(accessToken);
		this.accessToken = accessToken;
		try {
			FileUtils.writeStringToFile(this.tokenFile, accessToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getAccessToken(){
		return this.accessToken;
	}
}

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
 
/**
 * A program that demonstrates how to upload files from local computer
 * to a remote FTP server using Apache Commons Net API.
 * @author www.codejava.net
 */
public class FTPWrapper {
	
	private String server;
	private int port;
    private String user;
    private String pass;
    private FTPClient ftpClient;
    private File configFile = new File("deploy_config.txt");
    
    public FTPWrapper() throws IOException{
		String[] config = FileUtils.readFileToString(configFile).split("\n");
		this.server = config[0];
        this.port = Integer.parseInt(config[1]);
        this.user = config[2];
        this.pass = config[3];
        this.ftpClient = new FTPClient();
    }
    
    public FTPWrapper(String[] args) throws Exception{
    	if(args.length > 0){
    		this.server = args[0];
            this.port = Integer.parseInt(args[1]);
            this.user = args[2];
            this.pass = args[3];
            this.ftpClient = new FTPClient();
    	} else {
    		throw new Exception("missing arguments");	
    	}
    	
    }
    
    private void auth() throws SocketException, IOException{
    	this.ftpClient.connect(this.server, this.port);
        this.ftpClient.login(this.user, this.pass);
        this.ftpClient.enterLocalPassiveMode();
    }
    
    private void disconnect() throws IOException{
    	if (this.ftpClient.isConnected()) {
        	this.ftpClient.logout();
        	this.ftpClient.disconnect();
        }
    }
    
	public void upload(String localFilePath, String remoteFilePath) {
        try {

        	this.auth();
        	this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            File firstLocalFile = new File(localFilePath);
 
            String firstRemoteFile = remoteFilePath;
            InputStream inputStream = new FileInputStream(firstLocalFile);
 
            System.out.println("Start uploading file");
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("The first file is uploaded successfully.");
            }
 
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
            	this.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
	
	public boolean delete(String remoteFilePath) {
		Boolean result = false;
        try {

        	this.auth();
        	this.ftpClient.deleteFile(remoteFilePath);
 
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            result = false;
        } finally {
            try {
                this.disconnect();
                result = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                result = false;
            }
        }
		return result;
    }
 
}
package kr.me.ansr.image.upload;

public class Config {
	// File upload url (replace the ip with your server address)
//	public static final String FILE_UPLOAD_URL = "http://10.0.3.2:81/AndroidFileUpload/fileUpload.php";
//	public static final String FILE_UPLOAD_URL = "http://10.0.3.2:3000/updatePic/:userId";
//	public static final String FILE_GET_URL = "http://10.0.3.2:3000/getPic/:userId";
	public static final String FILE_UPLOAD_URL = "http://10.0.3.2:3000/updatePic/:userId";
	public static final String FILE_GET_URL = "http://10.0.3.2:3000/getPic/:userId/:size";
	// Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Dongne File Upload";
}

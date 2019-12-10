//package wolve.dms.libraries;
//
//import android.net.Uri;
//
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//
//import io.minio.MinioClient;
//import io.minio.errors.MinioException;
//import wolve.dms.utils.Util;
//
//import static wolve.dms.apiconnect.Api_link.HOST_IMAGE;
//import static wolve.dms.apiconnect.Api_link.HOST_IMAGE_PASS;
//import static wolve.dms.apiconnect.Api_link.HOST_IMAGE_USER;
//
//public class FileUploader {
//    String filename;
//    String file;
//    public FileUploader(Uri uri){
//        file = Util.getRealPathFromURI(uri);
//        filename =Util.getFilenameFromUri(uri);
//    }
//    public void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
//        try {
//            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
//            MinioClient minioClient = new MinioClient(HOST_IMAGE, HOST_IMAGE_USER, HOST_IMAGE_PASS);
//
//            // Check if the bucket already exists.
//            boolean isExist = minioClient.bucketExists("asiatrip");
//            if(isExist) {
//                System.out.println("Bucket already exists.");
//            } else {
//                // Make a new bucket called asiatrip to hold a zip file of photos.
//                minioClient.makeBucket("asiatrip");
//            }
//
//            // Upload the zip file to the bucket with putObject
//            minioClient.putObject("asiatrip",filename, file);
//            System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
//        } catch(MinioException e) {
//            System.out.println("Error occurred: " + e);
//        }
//    }
//}

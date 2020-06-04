//package wolve.dms.libraries.connectapi;
//
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//
//import io.minio.MinioClient;
//import io.minio.errors.MinioException;
//
//public class ImageUpload {
//    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
//        try {
//            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
//            MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
//
//            // Check if the bucket already exists.
//            boolean isExist =
//                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
//            if(isExist) {
//                System.out.println("Bucket already exists.");
//            } else {
//                // Make a new bucket called asiatrip to hold a zip file of photos.
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
//            }
//
//            // Upload the zip file to the bucket with putObject
//            minioClient.putObject("asiatrip","asiaphotos.zip", "/home/user/Photos/asiaphotos.zip", null);
//            System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
//        } catch(MinioException e) {
//            System.out.println("Error occurred: " + e);
//        }
//    }
//}

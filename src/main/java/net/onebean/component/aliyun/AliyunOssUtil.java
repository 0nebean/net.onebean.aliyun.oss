package net.onebean.component.aliyun;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import net.onebean.component.aliyun.prop.PropUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云OSS相关java API
 * @author 0neBean
 *
 */
public class AliyunOssUtil {

    public static final Map<String, String> contentType_map = new HashMap<String, String>();

    static {
        contentType_map.put("js", "application/x-javascript");
        contentType_map.put("css", "text/css");
        contentType_map.put("svg", "image/svg+xml");
        contentType_map.put("gif", "image/gif");
        contentType_map.put("jpg", "image/jpeg");
        contentType_map.put("png", "image/png");
        contentType_map.put("jpeg", "image/jpeg");
        contentType_map.put("bmp", "application/x-bmp");
        contentType_map.put("html", "text/html");
        contentType_map.put("ico", "image/x-icon");
        contentType_map.put("bmp", "application/x-bmp");
    }

    /**
     * 单例的oss链接
     * @author 0neBean
     */
    public static OSSClient ossClient = AliyunOssUtil.getOssClient();

    /**
     * 初始化oss连接
     * @author 0neBean
     * @return
     */
    protected static OSSClient getOssClient(){
        // 使用默认的OSS服务器地址创建OSSClient对象。
        /*oss参数系*/
        String endpoint = PropUtils.getInstance().getConfing("aliyun.oss.endpoint");
        String accessKeyId = PropUtils.getInstance().getConfing("aliyun.oss.accessKeyId");
        String secretAccessKey = PropUtils.getInstance().getConfing("aliyun.oss.secretAccessKey");
        return  new OSSClient(endpoint,accessKeyId, secretAccessKey);
    }


    /**
     *  把Bucket设置为所有人可读
     * @param bucketName 节点名称
     * @throws OSSException
     * @throws ClientException
     */
    public static void setBucketPublicReadable(String bucketName)throws OSSException, ClientException {
        //创建bucket
        ossClient.createBucket(bucketName);

        //设置bucket的访问权限，public-read-write权限
        ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    }

    /**
     *  上传文件
     * @param bucketName 节点名称
     * @param key 文件在oss上的key
     * @param filePath 文件的路径
     * @param contentType default "image/gif"
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    public static String uploadFile(String bucketName, String key, String filePath, String contentType) throws OSSException, ClientException, FileNotFoundException {
        File file = new File(filePath);
        contentType = contentType == null ? "image/gif" : contentType;
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        objectMeta.setContentType(contentType);
        InputStream input = new FileInputStream(file);
        PutObjectResult result =  ossClient.putObject(bucketName, key, input, objectMeta);
        return result.getETag();
    }

    /**
     * 下载文件
     * @param bucketName 节点名称
     * @param key 文件在oss上的key
     * @param filePath 下载目标的文件路径
     * @throws OSSException
     * @throws ClientException
     */
    public static void downloadFile(String bucketName, String key, String filePath) throws OSSException, ClientException {
        ossClient.getObject(new GetObjectRequest(bucketName, key),new File(filePath));
    }
    
    /**
     * 创建一个文件夹
     * @param bucketName 节点名称
     * @param folderPah 文件夹路径
     */
    public static void createFolder(String bucketName, String folderPah){
		ObjectMetadata objectMeta = new ObjectMetadata();
		byte[] buffer = new byte[0];
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		objectMeta.setContentLength(0);
		try {
		    ossClient.putObject(bucketName, folderPah, in, objectMeta);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
		    try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
    }


    /**
     *  删除一个Bucket和其中的Objects
     * @param bucketName 节点名称
     * @throws OSSException
     * @throws ClientException
     */
    public static void deleteBucket(String bucketName)
            throws OSSException, ClientException {

        ObjectListing ObjectListing = ossClient.listObjects(bucketName);
        List<OSSObjectSummary> listDeletes = ObjectListing
                .getObjectSummaries();
        for (int i = 0; i < listDeletes.size(); i++) {
            String objectName = listDeletes.get(i).getKey();
            // 如果不为空，先删除bucket下的文件
            ossClient.deleteObject(bucketName, objectName);
        }
        ossClient.deleteBucket(bucketName);
    }
    
    /**
     * 删除一个OSS文件对象
     * @param bucketName 节点名称
     * @param key 文件在oss上的key
     */
    public static void deleteObject(String bucketName, String key){
        ossClient.deleteObject(bucketName, key);
    }
}

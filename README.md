[![GitHub release](https://img.shields.io/badge/release-1.0.0-28a745.svg)](https://github.com/0nebean/com.alibaba.druid-0nebean.custom/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)


Introduction
---
- 对阿里云OSS API的调用封装

Documentation
---
- Config
#### chemical-el框架使用Apollo作为注册中心 ,以下是需要的配置namespace
[public-conf.aliyun](https://github.com/0nebean/public.conf/blob/master/conf/public-conf.aliyun.properties)

- API
#### 上传文件的方法 见AliyunOssUtil:
```java
    /**
     *  把Bucket设置为所有人可读
     * @param bucketName 节点名称
     * @throws OSSException
     * @throws ClientException
     */
    public static void setBucketPublicReadable(String bucketName);
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
    public static String uploadFile(String bucketName, String key, String filePath, String contentType);
    /**
    * 下载文件
    * @param bucketName 节点名称
    * @param key 文件在oss上的key
    * @param filePath 下载目标的文件路径
    * @throws OSSException
    * @throws ClientException
    */
    public static void downloadFile(String bucketName, String key, String filePath);
    /**
    * 创建一个文件夹
    * @param bucketName 节点名称
    * @param folderPah 文件夹路径
    */
    public static void createFolder(String bucketName, String folderPah);
    /**
    *  删除一个Bucket和其中的Objects
    * @param bucketName 节点名称
    * @throws OSSException
    * @throws ClientException
    */
    public static void deleteBucket(String bucketName);
    /**
    * 删除一个OSS文件对象
    * @param bucketName 节点名称
    * @param key 文件在oss上的key
    */
    public static void deleteObject(String bucketName, String key);
```


#### 获取文件的contentType方法
```java
    String contentType = AliyunOssUtil.contentType_map.get(".jpg");
```

package net.onebean.component.aliyun.prop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * oss配置读取类
 * @author 0neBean
 */
public class PropUtils {


    private static volatile PropUtils instance = null;
    protected static Map<String,String> conf = new HashMap<>();

    /**
     * 单例
     * @return
     */
    public static PropUtils getInstance() {
        if (instance == null) {
            synchronized (PropUtils.class) {
                if (instance == null) {
                    instance = new PropUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 默认无参构造器
     */
    private PropUtils(){
        init();
    }

    /**
     * 初始化方法
     */
    public static void init() {
        try {
            String path = getClassesPath(PropUtils.class);
            Properties properties = new Properties();
            // 使用InPutStream流读取properties文件
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path+"application.properties"));
            properties.load(bufferedReader);
            // 获取key对应的value值
            conf.put("aliyun.oss.endpoint",properties.getProperty("aliyun.oss.endpoint"));
            conf.put("aliyun.oss.accessKeyId",properties.getProperty("aliyun.oss.accessKeyId"));
            conf.put("aliyun.oss.secretAccessKey",properties.getProperty("aliyun.oss.secretAccessKey"));
            conf.put("aliyun.oss.bucketName",properties.getProperty("aliyun.oss.bucketName"));
            conf.put("aliyun.oss.host",properties.getProperty("aliyun.oss.host"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取classes路径
     * @param clazz
     * @return
     */
    public static String getClassesPath(Class clazz) {
        String path = clazz.getClassLoader().getResource("").getPath();
        try {
            return java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取配置信息
     * @param key
     * @return
     */
    public static String getConfing(String key){
        return conf.get(key);
    }
}

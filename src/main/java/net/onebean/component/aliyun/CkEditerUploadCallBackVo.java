package net.onebean.component.aliyun;

import java.util.Map;
/**
 * CkEditer上传文件返回VO
 * @author 0neBean
 */
public class CkEditerUploadCallBackVo {

    private Integer uploaded;
    private String fileName;
    private String url;
    /**
     * key message in error
     */
    private Map<String,String> error;

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }
}

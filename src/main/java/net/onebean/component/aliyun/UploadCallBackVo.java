package net.onebean.component.aliyun;

import java.util.Map;

/**
 * 上传文件返回VO
 * @author 0neBean
 */
public class UploadCallBackVo {

    private Boolean status;
    private Map<String,Object> data;
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

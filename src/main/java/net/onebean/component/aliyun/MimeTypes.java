package net.onebean.component.aliyun;


public enum MimeTypes {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    MP3("audio/mpeg"),
    MP4("video/mpeg4"),
    WEBP("image/webp"),
    WEBM("video/webm");

    private String value;
    MimeTypes(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public static String getByType(String type){
        if(type.equals("gif")){
            return MimeTypes.GIF.toString();
        }else if(type.equals("jpg")){
            return MimeTypes.JPEG.toString();
        }else if(type.equals("jpeg")){
            return MimeTypes.JPEG.toString();
        }else if(type.equals("png")){
            return MimeTypes.PNG.toString();
        }else if(type.equals("bmp")){
            return MimeTypes.BMP.toString();
        }else if(type.equals("mp4")){
            return MimeTypes.MP4.toString();
        }else if(type.equals("mp3")){
            return MimeTypes.MP3.toString();
        }else if(type.equals("webp")){
            return MimeTypes.WEBP.toString();
        }else if(type.equals("webm")){
            return MimeTypes.WEBM.toString();
        }else{
            return MimeTypes.GIF.toString();
        }
    }
}

package ai.learngram.video.payload;

public class RestResponse {

    public String message;
    public Integer status;

    public RestResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
    }
}

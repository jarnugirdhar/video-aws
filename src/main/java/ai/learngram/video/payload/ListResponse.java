package ai.learngram.video.payload;

import ai.learngram.video.model.Metadata;

import java.util.List;

public class ListResponse extends RestResponse {

    List<Metadata> data;

    public ListResponse(String message, Integer status, List<Metadata> data) {
        super(message, status);
        this.data = data;
    }

    public List<Metadata> getData() {
        return data;
    }

}

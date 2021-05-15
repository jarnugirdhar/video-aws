package ai.learngram.video.model;

import java.io.InputStream;

public class Media {

    private Metadata metadata;

    private int length;
    private InputStream dataStream;

    public Media(Metadata metadata, int length, InputStream dataStream) {
        this.metadata = metadata;
        this.length = length;
        this.dataStream = dataStream;
    }

    public Media(String title, int length, InputStream dataStream) {
        this.metadata = new Metadata(title, "");
        this.length = length;
        this.dataStream = dataStream;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public InputStream getDataStream() {
        return dataStream;
    }

    public int getLength() {
        return length;
    }
}

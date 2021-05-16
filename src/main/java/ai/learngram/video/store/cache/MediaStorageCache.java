package ai.learngram.video.store.cache;

import ai.learngram.video.model.Metadata;

import java.util.List;

public interface MediaStorageCache {

    public void add(Metadata mediaMetadata);

    public void add(List<Metadata> mediaMetadata);

    public Metadata search(String name);

}

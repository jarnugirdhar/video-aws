package ai.learngram.video.store.cache;

import ai.learngram.video.model.Metadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalMediaStorageCache implements MediaStorageCache {

    Map<String, Metadata> cache;

    public LocalMediaStorageCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Metadata mediaMetadata) {
        this.cache.put(mediaMetadata.getName(), mediaMetadata);
    }

    @Override
    public void add(List<Metadata> mediaMetadata) {
        mediaMetadata.forEach(metadata -> this.cache.put(metadata.getName(), metadata));
    }

    @Override
    public Metadata search(String name) {
        return this.cache.getOrDefault(name, new Metadata());
    }
}

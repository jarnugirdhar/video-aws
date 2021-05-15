package ai.learngram.video.service;

import ai.learngram.video.model.Media;
import ai.learngram.video.model.Metadata;
import ai.learngram.video.store.*;
import ai.learngram.video.store.api.MediaStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoStorageService {

    @Autowired
    MediaStorageClient storageClient;

    Map<String, Metadata> temporaryStorage;

    public VideoStorageService() {
        this.temporaryStorage = new HashMap<>();
    }

    public void upload(InputStream inputStream, int fileSize, String fileName, String thumbnail) throws MediaStoreException {
        Media media = new Media(new Metadata(fileName, thumbnail),
                fileSize,
                inputStream);

        storageClient.add(media);

        temporaryStorage.put(media.getMetadata().getName(), media.getMetadata());
    }

    public Mono<ResponseEntity<byte[]>> download(String fileName) throws MediaStoreException {
        Media media = storageClient.retrieve(fileName);
        try(InputStream inputStream = media.getDataStream()) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "video/mp4")
                    .header("Content-Length", String.valueOf(media.getLength()))
                    .body(inputStream.readAllBytes())
            );
        }
        catch (Exception ex) {
            throw new MediaStoreException(ex.getMessage());
        }
    }

    public List<Metadata> list(Integer pageSize, Integer page) throws MediaStoreException {
        return storageClient.listAll(pageSize, page);
    }

    public Metadata search(String name) throws MediaStoreException {
        if(temporaryStorage.containsKey(name)) {
            return temporaryStorage.get(name);
        }
        throw new MediaStoreException("Unable to find");
    }
}

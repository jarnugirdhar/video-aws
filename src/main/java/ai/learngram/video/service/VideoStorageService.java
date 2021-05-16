package ai.learngram.video.service;

import ai.learngram.video.exception.GeneralCustomException;
import ai.learngram.video.model.Media;
import ai.learngram.video.model.Metadata;
import ai.learngram.video.store.*;
import ai.learngram.video.store.cache.MediaStorageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

@Service
public class VideoStorageService {

    @Autowired
    MediaStorageClient storageClient;

    @Autowired
    MediaStorageCache storageCache;

    public void upload(InputStream inputStream, int fileSize, String fileName, String thumbnail) throws GeneralCustomException {
        Media media = new Media(new Metadata(fileName, thumbnail),
                fileSize,
                inputStream);

        storageClient.add(media);
        storageCache.add(media.getMetadata());
    }

    public Mono<ResponseEntity<byte[]>> download(String fileName) throws GeneralCustomException {
        /*
        *  Additional check for sanity.
         */
        search(fileName);

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
            throw new GeneralCustomException(ex.getMessage());
        }
    }

    public List<Metadata> list(Integer pageSize, Integer page) throws GeneralCustomException {
        return storageClient.listAll(pageSize, page);
    }

    public Metadata search(String name) throws GeneralCustomException {
        if(name.isBlank()) {
            throw new GeneralCustomException("Invalid input");
        }
        Metadata cachedMetadata = storageCache.search(name);
        if(cachedMetadata.getName() != null) {
            return cachedMetadata;
        }
        else {
            Metadata retrieved = storageClient.search(name);
            if(retrieved.getName() != null) {
                storageCache.add(retrieved);
                return retrieved;
            }
        }
        throw new GeneralCustomException("Could not find file with name: " + name);
    }
}

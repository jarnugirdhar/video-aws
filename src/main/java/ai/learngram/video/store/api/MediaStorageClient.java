package ai.learngram.video.store.api;

import ai.learngram.video.model.Media;
import ai.learngram.video.model.Metadata;
import ai.learngram.video.store.MediaStoreException;

import java.util.List;

public interface MediaStorageClient {

    public void add(Media media) throws MediaStoreException;

    public Media retrieve(String title) throws MediaStoreException;

    public List<Metadata> listAll(Integer pageSize, Integer page) throws MediaStoreException;

}

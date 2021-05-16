package ai.learngram.video.store;

import ai.learngram.video.model.Media;
import ai.learngram.video.model.Metadata;
import ai.learngram.video.exception.GeneralCustomException;

import java.util.List;

public interface MediaStorageClient {

    public void add(Media media) throws GeneralCustomException;

    public Media retrieve(String title) throws GeneralCustomException;

    public List<Metadata> listAll(Integer pageSize, Integer page) throws GeneralCustomException;

    public List<Metadata> listAll() throws GeneralCustomException;

    public Metadata search(String title) throws GeneralCustomException;

}

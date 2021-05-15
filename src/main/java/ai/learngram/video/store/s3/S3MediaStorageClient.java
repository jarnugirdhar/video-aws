package ai.learngram.video.store.s3;

import ai.learngram.video.model.Media;
import ai.learngram.video.store.api.MediaStorageClient;
import ai.learngram.video.store.MediaStoreException;
import ai.learngram.video.model.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3MediaStorageClient implements MediaStorageClient {

    @Value("${aws.s3.region}")
    private String awsRegion;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.file-part-size}")
    private int partSize;

    @Override
    public void add(Media media) throws MediaStoreException {
        try(S3Client client = S3ClientFactory.getClient(awsRegion)) {
            String tag = "name=" + media.getMetadata().getName() + "&thumbnail=" + media.getMetadata().getThumbnail();

            CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest
                    .builder()
                    .bucket(bucket)
                    .key(media.getMetadata().getName())
                    .tagging(tag)
                    .build();

            CreateMultipartUploadResponse multipartUploadResponse = client.createMultipartUpload(multipartUploadRequest);
            String uploadId = multipartUploadResponse.uploadId();

            List<CompletedPart> uploadedParts = uploadParts(client, media, uploadId);

            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload
                    .builder()
                    .parts(uploadedParts)
                    .build();
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest
                    .builder()
                    .bucket(bucket)
                    .key(media.getMetadata().getName())
                    .uploadId(uploadId)
                    .multipartUpload(completedMultipartUpload)
                    .build();

            client.completeMultipartUpload(completeRequest);
        }
        catch (Exception ex) {
            throw new MediaStoreException(ex.getMessage());
        }
    }

    private List<CompletedPart> uploadParts(S3Client client, Media media, String uploadId) throws MediaStoreException {
        List<CompletedPart> parts = new ArrayList<>();
        int[] fileSize = breakFileSize(media.getLength());
        int bufferSize = 0;
        try(InputStream inputStream = media.getDataStream()) {
            int partNumber = 1;
            byte[] fixedSizeBuffer = new byte[fileSize[0]];
            while((bufferSize = inputStream.read(fixedSizeBuffer)) > 0) {
                if(bufferSize == fileSize[1]) {
                    byte[] excessBuffer = new byte[fileSize[1]];
                    System.arraycopy(fixedSizeBuffer, 0, excessBuffer, 0, fileSize[1]);
                    parts.add(uploadPart(client, media.getMetadata().getName(), uploadId, excessBuffer, partNumber));
                }
                else {
                    parts.add(uploadPart(client, media.getMetadata().getName(), uploadId, fixedSizeBuffer, partNumber));
                }
                partNumber += 1;
            }
        }
        catch (Exception ex) {
            throw new MediaStoreException(ex.getMessage());
        }
        return parts;
    }

    private CompletedPart uploadPart(S3Client client, String fileName, String uploadId, byte[] byteBuffer, int partNumber) {

        UploadPartRequest partRequest = UploadPartRequest
                .builder()
                .bucket(bucket)
                .key(fileName)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        String eTag = client.uploadPart(partRequest, RequestBody.fromBytes(byteBuffer)).eTag();

        System.out.println(eTag);

        return CompletedPart
                .builder()
                .partNumber(partNumber)
                .eTag(eTag)
                .build();
    }

    private int[] breakFileSize(int fileSize) {
        int totalParts = fileSize/partSize;
        int excessPartSize = 0;
        if(totalParts > 0) {
            excessPartSize = fileSize - totalParts * partSize;
        }
        else {
            partSize = fileSize;
        }
        return new int[] { partSize, excessPartSize };
    }

    @Override
    public Media retrieve(String title) throws MediaStoreException {
        try(S3Client client = S3ClientFactory.getClient(awsRegion)) {
            GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .key(title)
                    .bucket(bucket)
                    .build();
            ResponseBytes<GetObjectResponse> response = client.getObjectAsBytes(getObjectRequest);
            int fileSize = Math.toIntExact(response.response().contentLength());
            return new Media(title, fileSize, response.asInputStream());
        }
        catch (Exception ex) {
            throw new MediaStoreException(ex.getMessage());
        }
    }

    @Override
    public List<Metadata> listAll(Integer pageSize, Integer page) throws MediaStoreException {
        try(S3Client client = S3ClientFactory.getClient(awsRegion)) {
            ListObjectsV2Request request = ListObjectsV2Request
                    .builder()
                    .bucket(bucket)
                    .maxKeys(pageSize)
                    .build();
            ListObjectsV2Iterable response = client.listObjectsV2Paginator(request);
            List<S3Object> fetchedObjects = new ArrayList<>();
            for (ListObjectsV2Response result : response) {
                page -= 1;
                if (page == 0) {
                    fetchedObjects.addAll(result.contents());
                    break;
                }
            }
            return extractMetadata(client, fetchedObjects);
        }
        catch (Exception ex) {
            throw new MediaStoreException(ex.getMessage());
        }
    }

    private List<Metadata> extractMetadata(S3Client client, List<S3Object> s3Objects) {
        List<Metadata> result = new ArrayList<>();
        s3Objects.stream().map(S3Object::key).forEach(x -> {
            GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest
                    .builder()
                    .key(x)
                    .bucket(bucket)
                    .build();
            GetObjectTaggingResponse tags = client.getObjectTagging(getTaggingRequest);
            List<Tag> retrievedTags = tags.tagSet();
            Metadata metadata = new Metadata();
            retrievedTags.forEach(tag -> {
                if(tag.key().equals("name")) {
                    metadata.setName(tag.value());
                }
                if(tag.key().equals("thumbnail")) {
                    metadata.setThumbnail(tag.value());
                }
            });
            result.add(metadata);
        });
        return result;
    }
}

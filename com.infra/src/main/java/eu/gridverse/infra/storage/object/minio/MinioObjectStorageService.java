package eu.gridverse.infra.storage.object.minio;
import eu.gridverse.infra.storage.object.ObjectStorageService; import io.minio.*; import java.io.InputStream; import java.util.Optional;
public final class MinioObjectStorageService implements ObjectStorageService {
    private final MinioClient client; public MinioObjectStorageService(MinioClient client){this.client=client;}
    public void put(String bucket,String key,InputStream content,long size,String type){try{if(!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());client.putObject(PutObjectArgs.builder().bucket(bucket).object(key).stream(content,size,-1).contentType(type).build());}catch(Exception e){throw new IllegalStateException("MinIO put failed",e);}}
    public Optional<InputStream> get(String bucket,String key){try{return Optional.of(client.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build()));}catch(Exception e){return Optional.empty();}}
    public void delete(String bucket,String key){try{client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());}catch(Exception e){throw new IllegalStateException("MinIO delete failed",e);}}
}

package eu.gridverse.srv.cnm.services.config;
import eu.gridverse.infra.event.EventPublisherService;
import eu.gridverse.infra.storage.document.DocumentRepositoryService;
import eu.gridverse.infra.storage.object.ObjectStorageService;
import eu.gridverse.srv.cnm.services.service.CnmImportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import eu.gridverse.powsbl.network.CnmNetworkImportAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import java.io.*; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration public class CnmConfiguration implements WebMvcConfigurer {
    @Bean @ConditionalOnMissingBean ObjectStorageService localObjectStorageService() { return new ObjectStorageService() { private final Map<String,byte[]> values=new ConcurrentHashMap<>(); public void put(String bucket,String key,InputStream content,long size,String type){try{values.put(bucket+"/"+key,content.readAllBytes());}catch(IOException e){throw new UncheckedIOException(e);}} public Optional<InputStream> get(String bucket,String key){byte[] value=values.get(bucket+"/"+key);return value==null?Optional.empty():Optional.of(new ByteArrayInputStream(value));} public void delete(String bucket,String key){values.remove(bucket+"/"+key);} }; }
    @Bean @ConditionalOnMissingBean DocumentRepositoryService localDocumentRepositoryService() { return new DocumentRepositoryService() { private final Map<String,Object> values=new ConcurrentHashMap<>(); public <T>T save(String index,String id,T value){values.put(index+"/"+id,value);return value;} public <T>Optional<T> find(String index,String id,Class<T> type){return Optional.ofNullable(values.get(index+"/"+id)).filter(type::isInstance).map(type::cast);} public <T>List<T> search(String index,eu.gridverse.infra.storage.document.DocumentSearchRequest request,Class<T> type){return values.entrySet().stream().filter(e->e.getKey().startsWith(index+"/")&&type.isInstance(e.getValue())).map(e->type.cast(e.getValue())).toList();} }; }
    @Bean @ConditionalOnMissingBean EventPublisherService localEventPublisherService() { return (topic,key,event)->{ }; }
    @Bean CnmImportService cnmImportService(ObjectStorageService objects, DocumentRepositoryService documents, EventPublisherService events, CnmNetworkImportAdapter networks) { return new CnmImportService(objects, documents, events, networks); }
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry){registry.addResourceHandler("/openapi/**").addResourceLocations("classpath:/openapi/");}
}

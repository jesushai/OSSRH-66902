package storage.qiniu.entity;

import com.lemon.framework.storage.model.StorageInfo;
import lombok.Data;

@Data
public class MockStorageEntity implements StorageInfo {

    private Long id;

    private String key;

    private String name;

    private String type;

    private Long size;

    private String url;
}

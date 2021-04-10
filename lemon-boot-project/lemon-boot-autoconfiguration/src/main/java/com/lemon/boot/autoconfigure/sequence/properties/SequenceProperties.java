package com.lemon.boot.autoconfigure.sequence.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Data
@ConfigurationProperties(prefix = "zh.db.sequence")
public class SequenceProperties {

    private int nodeId = -1;

}

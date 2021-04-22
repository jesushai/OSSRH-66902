package com.lemon.boot.autoconfigure.sequence.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Data
@ConfigurationProperties(prefix = "zh.db.sequence")
public class SequenceProperties {

    private int nodeId = -1;

}

package mp.module.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 品牌商表
 * </p>
 *
 * @author hai-zhang
 * @since 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("base_brand")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id_", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 品牌商名称
     */
    @TableField("name_")
    private String name;

    /**
     * 品牌商简介
     */
    @TableField("desc_")
    private String desc;

    /**
     * 品牌商页的品牌商图片
     */
    @TableField("pic_url_")
    private String picUrl;

    /**
     * 顺序
     */
    @TableField("sort_order_")
    private Integer sortOrder;

    /**
     * 品牌商的商品低价，仅用于页面展示
     */
    @TableField("floor_price_")
    private BigDecimal floorPrice;

    /**
     * 创建时间
     */
    @TableField(value = "add_time_", fill = FieldFill.INSERT)
    private Timestamp addTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time_", fill = FieldFill.INSERT_UPDATE)
    @Version
    private Timestamp updateTime;

    /**
     * 逻辑删除
     */
    @TableField("deleted_")
    @TableLogic
    private Boolean deleted;


}

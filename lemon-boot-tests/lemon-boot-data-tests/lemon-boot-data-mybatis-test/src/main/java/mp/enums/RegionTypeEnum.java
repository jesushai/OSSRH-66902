package mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lemon.framework.db.enums.DbFieldEnum;
import com.lemon.framework.db.enums.DbFieldIntegerEnum;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2021-4-10
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RegionTypeEnum implements DbFieldIntegerEnum {

    Province(1, "省"),
    City(2, "市"),
    District(3, "县区");

    @EnumValue //写入到数据库里的枚举值
//    @JsonValue //枚举类的json返回值
    private final int value;
    private final String display;

    RegionTypeEnum(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    /**
     * Json反序列化为枚举的时候需要依据Json中value的值来决定反序列化为哪个枚举
     */
    @JsonCreator
    public static RegionTypeEnum forValues(@JsonProperty("value") String value) throws Exception {
        return DbFieldEnum.fromValue(RegionTypeEnum.class, value);
    }
}

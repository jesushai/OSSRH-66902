package enums;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public enum Enum1 implements BaseEnum {
    A(0, "A"), B(1, "B");

    Enum1(int value, String display) {
        this.value = value;
        this.display = display;
    }

    private int value;

    private String display;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        return display;
    }
}

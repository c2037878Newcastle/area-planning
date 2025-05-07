package uk.co.rhilton.townplanning.setting;

import uk.co.rhilton.api.setting.Setting;

public class TownSettings {

    public static final Setting<Integer> SHOP_DISTANCE = Setting.Integer("shop_distance", () -> 80);

    public static final Setting<Integer> SHOP_HEIGHT = Setting.Integer("shop_height", () -> 16);
    public static final Setting<Integer> SHOP_WIDTH = Setting.Integer("shop_width", () -> 16);
    public static final Setting<Integer> SHOP_VALUE = Setting.Integer("shop_value", () -> 10_000);

    public static final Setting<Integer> HOUSE_HEIGHT = Setting.Integer("house_height", () -> 18);
    public static final Setting<Integer> HOUSE_WIDTH = Setting.Integer("house_width", () -> 18);
    public static final Setting<Integer> HOUSE_VALUE = Setting.Integer("house_value", () -> 14_200);


}

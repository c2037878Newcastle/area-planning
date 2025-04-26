package uk.co.rhilton.townplanning;

import uk.co.rhilton.api.persist.Setting;

public class TownSettings {

    public static final Setting<Integer> SHOP_DISTANCE = Setting.Integer("shop_distance", () -> 50);

    public static final Setting<Integer> SHOP_HEIGHT = Setting.Integer("shop_height", () -> 10);
    public static final Setting<Integer> SHOP_WIDTH = Setting.Integer("shop_width", () -> 10);
    public static final Setting<Integer> SHOP_VALUE = Setting.Integer("shop_value", () -> 10);

    public static final Setting<Integer> HOUSE_HEIGHT = Setting.Integer("house_height", () -> 10);
    public static final Setting<Integer> HOUSE_WIDTH = Setting.Integer("house_width", () -> 10);
    public static final Setting<Integer> HOUSE_VALUE = Setting.Integer("house_value", () -> 1000);


}

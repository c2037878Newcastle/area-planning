package uk.co.rhilton.townplanning;

import uk.co.rhilton.api.persist.Setting;

public class TownSettings {

    public static final Setting<Integer> SHOP_DISTANCE = Setting.Integer("shop_distance", () -> 10);

}

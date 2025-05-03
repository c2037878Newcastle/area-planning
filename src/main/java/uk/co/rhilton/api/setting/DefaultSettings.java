package uk.co.rhilton.api.setting;

public class DefaultSettings {

    public static final Setting<Integer> TIME_LIMIT = Setting.Integer("time_limit", () -> 600);
    public static final Setting<Boolean> BINARY_SEARCH = Setting.Boolean("binary_search", () -> true);

}

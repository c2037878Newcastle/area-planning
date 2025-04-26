package uk.co.rhilton.api.persist;

public class DefaultSettings {

    public static final Setting<Integer> TIME_LIMIT = Setting.Integer("time_limit", () -> 600);

}

package uk.co.rhilton.api.persist;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class Setting<T> {

    private final Class<T> clazz;
    private final String id;
    private final Supplier<T> defaultValue;

    public Setting(Class<T> dataType, String id, Supplier<T> defaultValue) {
        this.clazz = dataType;
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String id() {
        return id;
    }

    public T getDefault() {
        return defaultValue.get();
    }

    public Supplier<T> supplyDefault() {
        return defaultValue;
    }


    public abstract T parse(JsonElement element);

    public abstract JsonElement save(T value);

    public static Setting<String> String(String id, Supplier<String> defaultValue) {
        return new Setting<>(String.class, id, defaultValue) {
            public String parse(JsonElement element) {
                return element.getAsString();
            }

            public JsonElement save(String value) {
                return new JsonPrimitive(value);
            }
        };
    }

    public static Setting<Integer> Integer(String id, IntSupplier defaultValue) {
        return new Setting<>(Integer.class, id, defaultValue::getAsInt) {
            public Integer parse(JsonElement element) {
                return element.getAsInt();
            }

            public JsonElement save(Integer value) {
                return new JsonPrimitive(value);
            }
        };
    }


}



package uk.co.rhilton.api.persist;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import javax.swing.*;
import java.awt.*;
import java.util.function.BooleanSupplier;
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

    public abstract Component settingField(T currentValue);

    public abstract T fromField(Component c);

    public static Setting<String> String(String id, Supplier<String> defaultValue) {
        return new Setting<>(String.class, id, defaultValue) {
            public String parse(JsonElement element) {
                return element.getAsString();
            }

            public JsonElement save(String value) {
                return new JsonPrimitive(value);
            }

            public Component settingField(String currentValue) {
                return new JTextField(currentValue);
            }

            public String fromField(Component c) {
                return ((JTextField) c).getText();
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

            public Component settingField(Integer currentValue) {
                return new JTextField(currentValue + "");
            }

            public Integer fromField(Component c) {
                return Integer.parseInt(((JTextField) c).getText());
            }
        };
    }

    public static Setting<Boolean> Boolean(String id, BooleanSupplier defaultValue) {
        return new Setting<>(Boolean.class, id, defaultValue::getAsBoolean) {
            public Boolean parse(JsonElement element) {
                return element.getAsBoolean();
            }

            public JsonElement save(Boolean value) {
                return new JsonPrimitive(value);
            }

            public Component settingField(Boolean currentValue) {
                return new JCheckBox("", currentValue);
            }

            public Boolean fromField(Component c) {
                return ((JCheckBox) c).isSelected();
            }
        };
    }


}



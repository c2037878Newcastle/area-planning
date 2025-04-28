package uk.co.rhilton.api.setting;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class SettingStorage {

    private JsonObject data;

    public SettingStorage(JsonObject src) {
        this.data = src.deepCopy();

        if (!this.data.has("settings"))
            this.data.add("settings", new JsonObject());
    }

    public SettingStorage() {
        this.data = new JsonObject();
        this.data.add("settings", new JsonObject());
    }

    private JsonObject settings() {
        return data.getAsJsonObject("settings");
    }

    public <T> T valueOf(Setting<T> setting) {
        var element = settings().get(setting.id());
        if (element == null) return setting.getDefault();
        return setting.parse(element);
    }

    public <T> SettingStorage save(Setting<T> setting, T value) {
        settings().add(setting.id(), setting.save(value));
        return this;
    }

    public <T> SettingStorage remove(Setting<T> setting) {
        settings().remove(setting.id());
        return this;
    }

    public boolean saveToFile(Path path) {
        try (
                var writer = new JsonWriter(Files.newBufferedWriter(path))
        ) {
            writer.setFormattingStyle(FormattingStyle.PRETTY);
            var gson = new Gson();
            gson.toJson(data, writer);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Optional<SettingStorage> fromFile(Path path) {
        try (
                var reader = new JsonReader(Files.newBufferedReader(path))
        ) {
            var obj = JsonParser.parseReader(reader);
            if (!obj.isJsonObject()) return empty();
            return of(new SettingStorage(obj.getAsJsonObject()));
        } catch (IOException e) {
            e.printStackTrace();
            return empty();
        }
    }
}

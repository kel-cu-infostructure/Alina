package ru.kelcuprum.alina.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.Alina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private String _filePath;
    private JsonObject _jsonConfiguration = new JsonObject();
    private final boolean _isFile;

    public Config(String filePath) {
        this._filePath = filePath;
        this._isFile = true;
        load();
    }

    public Config(JsonObject jsonConfiguration) {
        this._jsonConfiguration = jsonConfiguration;
        this._isFile = false;
    }
    /**
     * Сохранение конфигурации
     */
    public void save(){
        if(!_isFile) return;
        final Path configFile = Path.of(_filePath);

        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, _jsonConfiguration.toString());
        } catch (IOException e) {
            Alina.log(e.getLocalizedMessage(), Level.ERROR);
        }
    }

    /**
     * Загрузка файла конфигов
     */
    public void load(){
        if(!_isFile) return;
        final Path configFile = Path.of(_filePath);
        try{
            _jsonConfiguration = configFile.toFile().exists() ? GsonHelper.parse(Files.readString(configFile)) : new JsonObject();
        } catch (Exception e){
            Alina.log(e.getLocalizedMessage(), Level.ERROR);
            save();
        }

    }
    /**
     * Сброс конфигурации
     */
    public Config reset() {
        this._jsonConfiguration = new JsonObject();
        save();
        return this;
    }

    /**
     * Преобразование в JSON
     */
    public JsonObject toJSON() {
        return this._jsonConfiguration;
    }

    /**
     * Преобразование в JSON
     */
    public String toString() {
        return this._jsonConfiguration.toString();
    }

    /**
     * Проверка мембера на нул
     */
    public boolean isJsonNull(String type) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();

        if (!this._jsonConfiguration.has(type))
            return true;

        return this._jsonConfiguration.get(type).isJsonNull();
    }

    /**
     * Получение Boolean значения
     */
    public boolean getBoolean(String type, boolean defaultValue) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();
        if (!isJsonNull(type) && !(this._jsonConfiguration.get(type).getAsJsonPrimitive().isBoolean()))
            setBoolean(type, defaultValue);
        return isJsonNull(type) ? defaultValue : this._jsonConfiguration.get(type).getAsBoolean();
    }

    /**
     * Задать значения Boolean
     */
    public Config setBoolean(String type, boolean newValue) {
        this._jsonConfiguration.addProperty(type, newValue);
        save();
        return this;
    }

    /**
     * Получение String значения
     */

    public String getString(String type, String defaultValue) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();
        if (!isJsonNull(type) && !(this._jsonConfiguration.get(type).getAsJsonPrimitive().isString()))
            setString(type, defaultValue);
        return isJsonNull(type) ? defaultValue : this._jsonConfiguration.get(type).getAsString();
    }

    /**
     * Задать значения String
     */
    public Config setString(String type, String newValue) {
        this._jsonConfiguration.addProperty(type, newValue);
        save();
        return this;
    }

    /**
     * Получение Number значения
     */

    public Number getNumber(String type, Number defaultValue) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();
        if (!isJsonNull(type) && !(this._jsonConfiguration.get(type).getAsJsonPrimitive().isNumber()))
            setNumber(type, defaultValue);
        return isJsonNull(type) ? defaultValue : this._jsonConfiguration.get(type).getAsNumber();
    }

    /**
     * Задать значения Number
     */
    public Config setNumber(String type, Number newValue) {
        this._jsonConfiguration.addProperty(type, newValue);
        save();
        return this;
    }

    /**
     * Получение JsonObject значения
     */

    public JsonObject getJsonObject(String type, JsonObject defaultValue) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();
        if (!isJsonNull(type) && !(this._jsonConfiguration.get(type).getAsJsonPrimitive().isJsonObject()))
            setJsonObject(type, defaultValue);
        return isJsonNull(type) ? defaultValue : this._jsonConfiguration.get(type).getAsJsonObject();
    }

    /**
     * Задать значения JsonObject
     */
    public Config setJsonObject(String type, JsonObject newValue) {
        this._jsonConfiguration.add(type, newValue);
        save();
        return this;
    }

    /**
     * Получение JsonArray значения
     */

    public JsonArray getJsonArray(String type, JsonArray defaultValue) {
        if (this._jsonConfiguration == null) this._jsonConfiguration = new JsonObject();
        if (!isJsonNull(type) && !(this._jsonConfiguration.get(type).getAsJsonPrimitive().isJsonObject()))
            setJsonArray(type, defaultValue);
        return isJsonNull(type) ? defaultValue : this._jsonConfiguration.get(type).getAsJsonArray();
    }

    /**
     * Задать значения JsonArray
     */
    public Config setJsonArray(String type, JsonArray newValue) {
        this._jsonConfiguration.add(type, newValue);
        save();
        return this;
    }
}
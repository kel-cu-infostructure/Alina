package ru.kelcuprum.alina.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class GsonHelper {
    private static final Gson GSON = (new GsonBuilder()).create();

    public GsonHelper() {
    }

    public static boolean isStringValue(JsonObject jsonObject, String string) {
        return isValidPrimitive(jsonObject, string) && jsonObject.getAsJsonPrimitive(string).isString();
    }

    public static boolean isStringValue(JsonElement jsonElement) {
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
    }

    public static boolean isNumberValue(JsonObject jsonObject, String string) {
        return isValidPrimitive(jsonObject, string) && jsonObject.getAsJsonPrimitive(string).isNumber();
    }

    public static boolean isNumberValue(JsonElement jsonElement) {
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBooleanValue(JsonObject jsonObject, String string) {
        return isValidPrimitive(jsonObject, string) && jsonObject.getAsJsonPrimitive(string).isBoolean();
    }

    public static boolean isBooleanValue(JsonElement jsonElement) {
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isArrayNode(JsonObject jsonObject, String string) {
        return isValidNode(jsonObject, string) && jsonObject.get(string).isJsonArray();
    }

    public static boolean isObjectNode(JsonObject jsonObject, String string) {
        return isValidNode(jsonObject, string) && jsonObject.get(string).isJsonObject();
    }

    public static boolean isValidPrimitive(JsonObject jsonObject, String string) {
        return isValidNode(jsonObject, string) && jsonObject.get(string).isJsonPrimitive();
    }

    public static boolean isValidNode(@Nullable JsonObject jsonObject, String string) {
        return jsonObject != null && jsonObject.get(string) != null;
    }

    public static JsonArray parseArray(String string) {
        return parseArray((Reader)(new StringReader(string)));
    }

    public static JsonArray parseArray(Reader reader) {
        return (JsonArray)fromJson(GSON, reader, JsonArray.class, false);
    }
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> class_, boolean bl) {
        T object = fromNullableJson(gson, reader, class_, bl);
        if (object == null) {
            throw new JsonParseException("JSON data was null or empty");
        } else {
            return object;
        }
    }
    @Nullable
    public static <T> T fromNullableJson(Gson gson, Reader reader, Class<T> class_, boolean bl) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(bl);
            return gson.getAdapter(class_).read(jsonReader);
        } catch (IOException var5) {
            throw new JsonParseException(var5);
        }
    }
    public static JsonObject parse(String string) {
        return parse(string, false);
    }
    public static JsonObject parse(String string, boolean bl) {
        return parse((Reader)(new StringReader(string)), bl);
    }
    public static JsonObject parse(Reader reader, boolean bl) {
        return (JsonObject)fromJson(GSON, reader, JsonObject.class, bl);
    }
}

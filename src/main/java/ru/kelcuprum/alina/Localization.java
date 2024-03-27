package ru.kelcuprum.alina;

import com.google.gson.JsonObject;
import ru.kelcuprum.alina.config.GsonHelper;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Localization {
    public String filePath;
    public Localization(String filePath){
        this.filePath = filePath;
    }
    public JsonObject getJSONFile(){
        try {
            Path localizationFile = Path.of(filePath);
            if (localizationFile.toFile().exists()) {
                return GsonHelper.parse(Files.readString(localizationFile));
            } else {
                return new JsonObject();
            }
        } catch (Exception ex){
            Alina.log(ex.getLocalizedMessage());
            return new JsonObject();
        }
    }
    public JsonObject getJSONDefaultFile(){
        try {
            InputStream localizationFile = getClass().getResourceAsStream("/localization.json");
            if (localizationFile != null) {
                return GsonHelper.parse(new String(localizationFile.readAllBytes(), StandardCharsets.UTF_8));
            } else {
                return new JsonObject();
            }
        } catch (Exception ex){
            Alina.log(ex);
            return new JsonObject();
        }
    }
    public String getLocalization(String key){
        String text;
        try {
            if(!getJSONFile().has(key) || getJSONFile().get(key).isJsonNull()) {
                if(!getJSONDefaultFile().has(key) || getJSONDefaultFile().get(key).isJsonNull()) text = key;
                else text = getJSONDefaultFile().get(key).getAsString();
            }
            else text = getJSONFile().get(key).getAsString();
        } catch (Exception ex) {
            Alina.log(ex.getLocalizedMessage());
            return key;
        }
        return text;
    }
    // Заменить
    public void setLocalization(String type, String text){
        try {
            JsonObject JSONLocalization = getJSONFile();
            JSONLocalization.addProperty(type, text);
            Path localizationFile = Path.of(filePath);
            Files.createDirectories(localizationFile.getParent());
            Files.writeString(localizationFile, JSONLocalization.toString());
        } catch (Exception e){
            Alina.log(e);
        }
    }
}

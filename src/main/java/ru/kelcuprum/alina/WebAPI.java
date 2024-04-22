package ru.kelcuprum.alina;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import ru.kelcuprum.alina.config.GsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebAPI {
    public static HttpClient webClient = HttpClients.createDefault();

    public static String getString(String url) throws IOException, InterruptedException {
        try {
            if(webClient == null) webClient = HttpClients.createDefault();
            var httpget = new HttpGet(url);
            HttpResponse response = webClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    return new String(instream.readAllBytes(), UTF_8);
                }
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static JsonObject getJsonObject(String url) throws IOException, InterruptedException {
        return GsonHelper.parse(getString(url));
    }
    public static JsonArray getJsonArray(String url) throws IOException, InterruptedException {
        return GsonHelper.parseArray(getString(url));
    }
    public static String getTranslate(String text, String to, String from){
        try {
            if(webClient == null) webClient = HttpClients.createDefault();
            var httppost = new HttpPost(new URI(String.format("http://kelcu.local:1188/translate?token=0404Alina&text=%s&target_lang=%s", text, to)).toURL().toString());
            httppost.setHeader(new BasicHeader("Content-Type", "application/json"));
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("text", text));
            if(!from.isEmpty()) params.add(new BasicNameValuePair("source_lang", "Hello!"));
            params.add(new BasicNameValuePair("target_lang", to));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = webClient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    return new String(instream.readAllBytes(), UTF_8);
                }
            }
            return "";
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    };
}

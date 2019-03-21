package com.spawn.ai.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

import constants.ChatViewTypes;

public class JsonFileReader {

    private static JsonFileReader jsonFileReader;
    private String fileContents;
    private String fileName = "bot_data.json";

    public static JsonFileReader getInstance() {
        if (jsonFileReader == null) {
            jsonFileReader = new JsonFileReader();
        }
        return jsonFileReader;
    }

    public void fileName(String fileName) {
        this.fileName = fileName;
    }

    public String readFile(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            json = new String(bytes, "utf-8");
            this.fileContents = json;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public JSONObject convertToJson(String contents) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(contents);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    public String getJsonFromKey(String key, int i) {
        String message = "";
        try {
            switch (i) {
                case ChatViewTypes.CHAT_VIEW_BOT:
                    if (fileContents != null) {
                        JSONObject data = new JSONObject(fileContents);
                        JSONObject body = (data.has(key)) ? data.getJSONObject(key) : data.getJSONObject("default");
                        JSONArray jsonArray = body.getJSONArray("message");
                        int index = new Random().nextInt(jsonArray.length());
                        message = jsonArray.get(index).toString();
                        return message;
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return message;
    }

    public String getDefaultAnswer() {
        String message = "";
        if (fileContents != null) {
            try {
                JSONObject data = new JSONObject(fileContents);
                if (data.has("default")) {
                    JSONObject body = data.getJSONObject("default");
                    JSONArray jsonArray = body.getJSONArray("message");
                    int index = new Random().nextInt(jsonArray.length());
                    message = jsonArray.get(index).toString();
                } else {
                    message = "Sorry, I could not understand what you just said";
                }
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
                message = "Sorry, I could not understand what you just said";
            }
        }
        return message;
    }

    public String getFileContents() {
        return this.fileContents;
    }

}

package net.atherial.api.redis.utilities;

import com.google.gson.JsonParser;

public class JsonUtils {

    private static final JsonParser jsonParser;

    static {
        jsonParser = new JsonParser();
    }

    public static boolean isValidJson(String input) {

        try {
            jsonParser.parse(input);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static JsonParser getJsonParser() {
        return jsonParser;
    }
}

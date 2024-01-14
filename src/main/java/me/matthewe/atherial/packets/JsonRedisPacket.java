package me.matthewe.atherial.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.atherial.api.redis.AtherialRedisPacket;

/**
 * Created by Matthew E on 10/26/2019 at 8:24 PM for the project atherialapi
 */
public class JsonRedisPacket implements AtherialRedisPacket {
    private String packetName;
    private JsonObject jsonObject;

    public JsonRedisPacket(String packetName, JsonObject jsonObject) {
        this.packetName = packetName;
        this.jsonObject = jsonObject;
    }


    public String getPacketName() {
        return packetName;
    }

    public <T> JsonObject toJson(Class<T> clazz) {
        return new Gson().fromJson(new Gson().toJson(this, clazz), JsonObject.class);
    }

    public <T> T fromJson(Class<T> clazz) {
        return new Gson().fromJson(jsonObject, clazz);
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}

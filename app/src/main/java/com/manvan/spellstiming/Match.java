package com.manvan.spellstiming;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import androidx.annotation.RequiresApi;

public class Match {
    private static String BASE_URL = "https://euw1.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/";
    private long GameId;
    private String PlatformId;
    private String GameMode;
    private long MapId;
    private int QueueType;
    private JSONArray Participants;
    private long GameLength;
    private ApiQuery Api;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Match(Context context, String summid, Consumer<Void> callback) {
        Api = new ApiQuery(context);
        Log.i("Request", BASE_URL + summid);
        Log.i("Query", "launched");
        Consumer<JSONObject> cons = a -> {
            extract(a);
            callback.accept(null);
        };
        Api.query(BASE_URL + summid, cons);
    }

    public void extract(JSONObject data) {
        if (data!=null) {
            try {
                GameId = (long) data.get("gameId");
                PlatformId = (String) data.get("platformId");
                GameMode = (String) data.get("gameMode");
                MapId = (int) data.get("mapId");
                QueueType = (int) data.get("gameQueueConfigId");
                Participants = (JSONArray) data.get("participants");
                GameLength = (int) data.get("gameLength");
            } catch (JSONException e) {
                Log.w("JSON", "Error extracting summoner data from JSON Object");
                Log.w("JSON", e.toString());
            }
        }
    }

    public long getGameId() {return GameId;}
    public long getGameLength() {return GameLength;}
    public long getMapId() {return MapId;}
    public String getGameMode() {return GameMode;}
    public int getQueueType() {return QueueType;}
    public String getPlatformId() {return PlatformId;}
    public ApiQuery getApi() {return Api;}
    public JSONArray getParticipants() {return Participants;}
}



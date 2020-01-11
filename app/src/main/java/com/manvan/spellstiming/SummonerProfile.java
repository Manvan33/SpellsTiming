package com.manvan.spellstiming;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import androidx.annotation.RequiresApi;

public class SummonerProfile {
    private static String BASE_URL = "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
    private String Name;
    private String IconId;
    private int Lvl;
    private String AccountId;
    private String SummonerId;
    private ApiQuery Api;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SummonerProfile(Context context, String name, Consumer<Void> callback) {
        Api = new ApiQuery(context);
        Log.i("Request",BASE_URL+name);
        Log.i("Query","launched");
        Consumer<JSONObject> cons = a -> {
            extract(a);
            callback.accept(null);
        };
        Api.query(BASE_URL+name,cons);
    }
    public void extract(JSONObject data) {
        if (data != null) {
            try {
                Name = (String) data.get("name");
                AccountId = data.getString("accountId");
                Lvl = Integer.parseInt(data.getString("summonerLevel"));
                IconId = data.getString("profileIconId");
                SummonerId = data.getString("id");
            } catch (JSONException e) {
                Log.w("JSON", "Error extracting summoner data from JSON Object");
                Log.w("JSON", e.toString());
            }
        }
    }

    public int getLvl() {return Lvl;}
    public String getIconId() {return IconId;}
    public String getSummonerId() {return SummonerId;}
    public String getName() {return Name;}
}

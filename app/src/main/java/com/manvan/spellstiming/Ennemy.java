package com.manvan.spellstiming;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Ennemy {
    private String Name;
    private int ChampId;
    private String ChampName;
    private JSONArray Perks;
    private int SpellOneId;
    private int SpellTwoId;
    private int ActualCDOne;
    private int ActualCDTwo;
    private int MaxCDOne;
    private int MaxCDTwo;
    private String SpellOne;
    private String SpellTwo;
    private TextView SpellOneCd;
    private TextView SpellTwoCd;
    private boolean Cosmic;
    private Timer TimerOne;
    private Timer TimerTwo;
    private static JSONObject ChampionsList;
    private static JSONObject SpellsList;
    private ApiQuery Apiq;
    private static Context CONTEXTE;
    private final String PATCH = "9.24.2";
    private static Handler Hand;
    final String CHAMP_ICONs_URL = "https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/img/champion/";
    final String SPELL_ICONs_URL = "https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/img/spell/";


    public Ennemy(Context con) {
        Cosmic = false;
        MaxCDOne = 0;
        MaxCDTwo = 0;
        Hand = new Handler();
        this.CONTEXTE = con;
        Consumer<JSONObject> callA = a -> {
            try {
                ChampionsList = a.getJSONObject("data");
            } catch (JSONException e) {
                Log.w("ChampionList JSON",e.getMessage());
            }
        };
        Consumer<JSONObject> callB = a -> {
            try {
                SpellsList = a.getJSONObject("data");
            } catch (JSONException e) {
                Log.w("ChampionList JSON",e.getMessage());
            }
        };
        Apiq = new ApiQuery(con);
        Apiq.query("https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/data/en_US/champion.json",callA);
        Apiq.query("https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/data/en_US/summoner.json",callB);
    }

    public Ennemy(JSONObject src) {
        try {
            Name = src.getString("summonerName");
            ChampId = src.getInt("championId");
            Perks = src.getJSONObject("perks").getJSONArray("perkIds");
            SpellOneId = src.getInt("spell1Id");
            SpellTwoId = src.getInt("spell2Id");
            Iterator x = ChampionsList.keys();//Liste des clés de la liste des champion

            while (x.hasNext()) { //Recherche dans la liste des champions
                String key = (String) x.next(); //Clé de l'entrée
                if (ChampionsList.getJSONObject(key).getInt("key") == ChampId) { //Id de champion trouvé
                    ChampName = ChampionsList.getJSONObject(key).getString("id");//Récupère le nom du champion
                }
            }
            x = SpellsList.keys();
            Log.i("TEst:", x.toString());
            while (x.hasNext()) { //Recherche dans la liste des Spells
                String key = (String) x.next(); //Clé de l'entrée
                if (SpellsList.getJSONObject(key).getString("key").equals(String.valueOf(SpellOneId))) { //Id de champion trouvé
                    SpellOne = SpellsList.getJSONObject(key).getString("id");//Récupère le nom du spell 1
                    MaxCDOne = Integer.valueOf(SpellsList.getJSONObject(key).getString("cooldownBurn"));
                }
                else if (SpellsList.getJSONObject(key).getString("key").equals(String.valueOf(SpellTwoId))) { //Id de champion trouvé
                    SpellTwo = SpellsList.getJSONObject(key).getString("id");//Récupère le nom du spell 2
                    MaxCDTwo = Integer.valueOf(SpellsList.getJSONObject(key).getString("cooldownBurn"));
                }
            }
            for (int i = 0; i<Perks.length(); i++) {
                if (Perks.getInt(i) == 8347) {
                    Cosmic = true;
                    MaxCDOne = (int) Math.round(MaxCDOne * 0.95);
                    MaxCDTwo = (int) Math.round(MaxCDTwo * 0.95);

                }
            }
        } catch (JSONException e) {
            Log.w("EnnemyJson",e.getMessage());
        }
    }

    public LinearLayout getLayout(Context con) {
        ActualCDOne = 0;
        ActualCDTwo = 0;
        LayoutInflater inf = LayoutInflater.from(con);
        LinearLayout Layoute = (LinearLayout) inf.inflate(R.layout.ennemy, null);
        LinearLayout souLayoute = (LinearLayout) Layoute.getChildAt(1);
        TextView sumName = (TextView) Layoute.getChildAt(0);
        LinearLayout souSouLayoute = ((LinearLayout) souLayoute.getChildAt(0));
        ImageView champIcon = (ImageView) souSouLayoute.getChildAt(0);
        TextView champName = (TextView) souSouLayoute.getChildAt(1);

        RelativeLayout SpellOneIconLay = (RelativeLayout) souLayoute.getChildAt(2);
        ImageView SpellOneIcon = (ImageView) SpellOneIconLay.getChildAt(0);
        SpellOneCd = (TextView) SpellOneIconLay.getChildAt(1);
        RelativeLayout SpellTwoIconLay = (RelativeLayout) souLayoute.getChildAt(4);
        ImageView SpellTwoIcon = (ImageView) SpellTwoIconLay.getChildAt(0);
        SpellTwoCd = (TextView) SpellTwoIconLay.getChildAt(1);

        if (Cosmic) {
            champName.setTextColor(Color.parseColor("#0000FF"));
        }
        SpellOneIcon.setOnClickListener(v -> {
            if (ActualCDOne == 0) {
                ActualCDOne = MaxCDOne;
                SpellOneCd.setBackgroundColor(Color.parseColor("#A0010101"));
                TimerOne = new Timer();
                TimerOne.scheduleAtFixedRate(taskOne(),0,1000);
            }
            else {
                ActualCDOne = 0;
                clearnOne();
            }
        });

        SpellTwoIcon.setOnClickListener(v -> {
            if (ActualCDTwo == 0) {
                ActualCDTwo = MaxCDTwo;
                TimerTwo = new Timer();
                SpellTwoCd.setBackgroundColor(Color.parseColor("#A0010101"));
                TimerTwo.scheduleAtFixedRate(taskTwo(),0,1000);
            }
            else {
                ActualCDTwo = 0;
                clearTwo();
            }

        });

        sumName.setText(this.Name);
        champName.setText(this.ChampName);
        Picasso.get().load(CHAMP_ICONs_URL + this.ChampName + ".png").into(champIcon);
        Picasso.get().load(SPELL_ICONs_URL + this.SpellOne + ".png").into(SpellOneIcon);
        Picasso.get().load(SPELL_ICONs_URL + this.SpellTwo + ".png").into(SpellTwoIcon);
        return Layoute;
    }
    public void vibrer() {
        Vibrator vibre = (Vibrator) CONTEXTE.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibre.vibrate(VibrationEffect.createOneShot(300,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            vibre.vibrate(300);
        }
    }
    public void clearnOne() {
        SpellOneCd.setText("\n");
        SpellOneCd.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        TimerOne.cancel();
        TimerOne.purge();
        vibrer();
        Toast.makeText(CONTEXTE,ChampName+" has his "+SpellOne+" up !",Toast.LENGTH_LONG).show();
    }
    public void clearTwo() {
        SpellTwoCd.setText("\n");
        SpellTwoCd.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        vibrer();
        TimerTwo.cancel();
        TimerTwo.purge();
        Toast.makeText(CONTEXTE,ChampName+" has his "+SpellTwo+" up !",Toast.LENGTH_LONG).show();
    }
    public TimerTask taskOne() {
        return new TimerTask() {
            @Override
            public void run() {
                Hand.post(() -> {
                    SpellOneCd.setText("\n");
                    if (ActualCDOne > 0) {
                        SpellOneCd.append(String.valueOf(ActualCDOne));
                        ActualCDOne--;
                    }
                    else {
                       clearnOne();
                    }
                }
                );
            }
        };
    }
    public TimerTask taskTwo() {
        return new TimerTask() {
            @Override
            public void run() {
                Hand.post(() -> {
                    SpellTwoCd.setText("\n");
                    if (ActualCDTwo > 0) {
                        SpellTwoCd.append(String.valueOf(ActualCDTwo));
                        ActualCDTwo--;
                    }
                    else {
                        clearTwo();
                    }
                });
            }
        };
    }

    public String toString() {
        return "Name:" +
                Name +
                "\nSpellOne : " +
                SpellOne +
                "\nSpellTwo : " +
                SpellTwo;
    }
}
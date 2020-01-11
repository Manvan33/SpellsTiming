package com.manvan.spellstiming;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private static String PATCH = "9.24.2";
    private SummonerProfile Profile;
    private Match Game;
    private ImageView ProfileIcon;
    private Ennemy[] Ennemies;
    private ApiQuery ApiQ;
    private JSONArray QueueTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getString(R.string.API_KEY);
        setContentView(R.layout.activity_main);
        ProfileIcon = (ImageView) findViewById(R.id.ProfileIcon);

        Consumer<JSONArray> callC = c -> {
            try {
                PATCH = c.getString(0);
                Log.i("getting PATCH",PATCH);
            } catch (JSONException e) {
                Log.i("getting PATCH","failed");
            }
        };
        Consumer<JSONArray> callB = a -> {
            QueueTypes = a;
            Log.i("Queue",QueueTypes.toString());
        };
        ApiQ = new ApiQuery(this,getString(R.string.API_KEY));
        ApiQ.queryA("https://static.developer.riotgames.com/docs/lol/queues.json",callB);
        ApiQ.queryA("https://ddragon.leagueoflegends.com/api/versions.json",callC);
        //Picasso.get().load("https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/img/profileicon/588.png").into(ProfileIcon);
        new Ennemy(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getProfile(View v) {
        EditText edtSumm = findViewById(R.id.SummonerNameField);
        Consumer<Void> callback = a -> showProfile();
        Profile = new SummonerProfile(getApplicationContext(), edtSumm.getText().toString(),callback);
    }
    public void showProfile() {
        TextView TextSumm = (TextView) findViewById(R.id.SummText);
        if (Profile.getName() != null) {
            TextSumm.setText(Profile.getName());
            TextSumm.append("\n" + Profile.getLvl());
            String baseUrl = "https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/img/profileicon/";
            Picasso.get().load(baseUrl + Profile.getIconId() + ".png").into(ProfileIcon);
            Log.i("icon", Profile.getIconId());
        }
        else TextSumm.setText("Unknown Summoner :/");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findMatch(TextSumm);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findMatch(View v) {
            LinearLayout layout = findViewById(R.id.ParticipantsLayout);
            layout.removeAllViews();
            Consumer<Void> callback = a -> showMatch();
            Game = new Match(this, Profile.getSummonerId(), callback);
    }
    public void showMatch() {
        TextView txtGameType = findViewById(R.id.txtGameType);
        LinearLayout layout = findViewById(R.id.ParticipantsLayout);
        Ennemies = new Ennemy[10];
        String QueueName = null;
        int i = 0;
        if (Game.getPlatformId() != null) {
            boolean trackable = true;
            JSONObject obj;
            while (!QueueTypes.isNull(i)) {
                try {
                    obj = (JSONObject) QueueTypes.get(i);
                    if (obj.getInt("queueId") == Game.getQueueType()) {
                        QueueName = obj.getString("description").substring(0, obj.getString("description").indexOf("games"));
                    }
                } catch (JSONException e) {
                    Log.i("Queue JSON Exception", e.toString());
                    QueueName = "error";
                } catch (StringIndexOutOfBoundsException e) {
                    QueueName = ("Custom \nNo data can be retrieved from this type of match.\n(︶︹︶)");
                    trackable = false;
                }
                finally {
                    i++;
                }
            }
            if (QueueName != null) {
                txtGameType.setText(QueueName);
            }
            if (trackable) {
                i = 0;
                JSONObject player;
                int team = 0;
                while (!Game.getParticipants().isNull(i)) {//Récupère l'id de la team ennemie
                    try {
                        player = (JSONObject) Game.getParticipants().get(i);
                        if (player.getString("summonerName").equals(Profile.getName())) {
                            team = player.getInt("teamId");
                        }
                    } catch (JSONException e) {
                        Log.i("Match Loading JSONErr", e.toString());
                    }
                    i++;
                }
                i = 0;
                int j = 0;
                String baseUrl = "https://ddragon.leagueoflegends.com/cdn/"+PATCH+"/img/champion/";
                while (!Game.getParticipants().isNull(i)) { //Parcourt les participants et affiche les ennemis
                    try {
                        player = (JSONObject) Game.getParticipants().get(i); //Chaque participant contenu dans player
                        if (player.getInt("teamId")!=team) { //Le participant n'est pas dans l'équipe du Summoner
                            Ennemies[j] = new Ennemy(player);
                            layout.addView(Ennemies[j].getLayout(getApplicationContext()));
                            j++;
                        }
                    } catch (JSONException e) {
                        Log.i("Match Loading JSONErr", e.toString());
                    } finally {
                        i++;
                    }
                }
            }
        }
        else txtGameType.setText("No Game Found :/");
    }
}

package com.manvan.spellstiming;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApiQuery api = new ApiQuery(appContext);
        Consumer<JSONArray> callB = a -> {
            Log.i("Queue",a.toString());
        };
        api.queryA("https://ddragon.leagueoflegends.com/api/versions.json",callB);
        assertEquals("com.manvan.spellstiming", appContext.getPackageName());
    }
}

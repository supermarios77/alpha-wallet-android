package com.alphawallet.app;


import static androidx.test.espresso.Espresso.setFailureHandler;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.alphawallet.app.ui.SplashActivity;
import com.alphawallet.app.util.CustomFailureHandler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public abstract class BaseE2ETest
{
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            setFailureHandler(new CustomFailureHandler(description.getMethodName(), InstrumentationRegistry.getInstrumentation().getTargetContext()));
        }
    };

//    @Rule
//    public ActivityScenarioRule<SplashActivity> activityScenarioRule
//            = new ActivityScenarioRule<>(SplashActivity.class);

    @Before
    public void setUp() {
        // Implies the use of ActivityScenario, instead of ActivityScenarioRule or ActivityTestRule
        ActivityScenario.launch(SplashActivity.class).onActivity(activity -> {
            activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        });
    }
}
package com.alphawallet.app;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.alphawallet.app.util.ReleaseTree;
import com.walletconnect.walletconnectv2.client.WalletConnect;
import com.walletconnect.walletconnectv2.client.WalletConnectClient;

import java.util.Arrays;
import java.util.Stack;

import dagger.hilt.android.HiltAndroidApp;
import io.realm.Realm;
import timber.log.Timber;

@HiltAndroidApp
public class App extends Application
{
    private static App mInstance;
    private Stack<Activity> activityStack = new Stack<>();

    public static App getInstance()
    {
        return mInstance;
    }

    public Activity getTopActivity()
    {
        return activityStack.peek();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        Realm.init(this);

        if (BuildConfig.DEBUG)
        {
            Timber.plant(new Timber.DebugTree());
        }
        else
        {
            Timber.plant(new ReleaseTree());
        }

        int defaultTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", C.THEME_AUTO);

        if (defaultTheme == C.THEME_LIGHT)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
        else if (defaultTheme == C.THEME_DARK)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        }
        else
        {
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int mode = uiModeManager.getNightMode();
            if (mode == UiModeManager.MODE_NIGHT_YES)
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            }
            else if (mode == UiModeManager.MODE_NIGHT_NO)
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
        }

        initWalletConnectV2Client();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {
            }

            @Override
            public void onActivityDestroyed(Activity activity)
            {
            }

            @Override
            public void onActivityStarted(Activity activity)
            {
            }

            @Override
            public void onActivityResumed(Activity activity)
            {
                activityStack.push(activity);
            }

            @Override
            public void onActivityPaused(Activity activity)
            {
                pop(activity);
            }

            @Override
            public void onActivityStopped(Activity activity)
            {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {
            }
        });
    }

    @NonNull
    private WalletConnect.Model.AppMetaData getAppMetaData()
    {

        String name = getString(R.string.app_name);
        String url = "https://alphawallet.com";
        String[] icons = {"https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"};

        String description = "The ultimate Web3 Wallet to power your tokens.";
        return new WalletConnect.Model.AppMetaData(name, description, url, Arrays.asList(icons));
    }

    private void initWalletConnectV2Client()
    {
        WalletConnect.Model.AppMetaData appMetaData = getAppMetaData();
        WalletConnect.Params.Init init = new WalletConnect.Params.Init(this,
                "wss://relay.walletconnect.com/?projectId=40c6071febfd93f4fe485c232a8a4cd9",
                true,
                appMetaData);

        WalletConnectClient.INSTANCE.initialize(init, e ->
        {
            Timber.tag("AlphaWallet").d("Init failed: %s", e.getMessage());
            return null;
        });
    }

    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        WalletConnectClient.INSTANCE.shutdown();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        activityStack.clear();
        WalletConnectClient.INSTANCE.shutdown();
    }

    private void pop(Activity activity)
    {
        activityStack.pop();
    }
}

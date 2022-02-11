package com.alphawallet.app.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.alphawallet.app.App;
import com.alphawallet.app.entity.walletconnect.WalletConnectV2SessionItem;
import com.alphawallet.app.ui.WalletConnectV2Activity;
import com.alphawallet.app.widget.SignMethodDialog;
import com.walletconnect.walletconnectv2.client.WalletConnect;
import com.walletconnect.walletconnectv2.client.WalletConnectClient;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AWWalletConnectClient implements WalletConnectClient.WalletDelegate
{
    public static WalletConnect.Model.SessionProposal sessionProposal;

    private static final String TAG = "AlphaWallet";
    private Context context;

    public AWWalletConnectClient(Context context)
    {
        this.context = context;
    }

    @Override
    public void onSessionDelete(@NonNull WalletConnect.Model.DeletedSession deletedSession)
    {
    }

    @Override
    public void onSessionNotification(@NonNull WalletConnect.Model.SessionNotification sessionNotification)
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSessionProposal(@NonNull WalletConnect.Model.SessionProposal sessionProposal)
    {
        AWWalletConnectClient.sessionProposal = sessionProposal;
        Intent intent = new Intent(context, WalletConnectV2Activity.class);
        intent.putExtra("session", WalletConnectV2SessionItem.from(sessionProposal));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSessionRequest(@NonNull WalletConnect.Model.SessionRequest sessionRequest)
    {
        String method = sessionRequest.getRequest().getMethod();

        WalletConnect.Model.SettledSession settledSession = getSession(sessionRequest.getTopic());
        if ("personal_sign".equals(method))
        {
            showSignDialog(sessionRequest, settledSession);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSignDialog(WalletConnect.Model.SessionRequest sessionRequest, WalletConnect.Model.SettledSession settledSession)
    {
        Activity topActivity = App.getInstance().getTopActivity();
        topActivity.runOnUiThread(() ->
        {
            SignMethodDialog signMethodDialog = new SignMethodDialog(topActivity);
            signMethodDialog.show();
        });
    }

    private WalletConnect.Model.SettledSession getSession(String topic)
    {
        List<WalletConnect.Model.SettledSession> listOfSettledSessions = WalletConnectClient.INSTANCE.getListOfSettledSessions();
        for (WalletConnect.Model.SettledSession session : listOfSettledSessions)
        {
            if (session.getTopic().equals(topic))
            {
                return session;
            }
        }
        return null;
    }
}

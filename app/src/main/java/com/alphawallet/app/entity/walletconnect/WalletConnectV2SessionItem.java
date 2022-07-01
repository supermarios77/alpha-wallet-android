package com.alphawallet.app.entity.walletconnect;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.walletconnect.walletconnectv2.client.Sign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.RequiresApi;

import timber.log.Timber;

public class WalletConnectV2SessionItem extends WalletConnectSessionItem implements Parcelable
{
    public boolean settled;
    public final List<String> chains = new ArrayList<>();
    public final List<String> wallets = new ArrayList<>();
    public final List<String> methods = new ArrayList<>();
    public WalletConnectV2SessionItem(Sign.Model.Session s)
    {
        super();
        name = Objects.requireNonNull(s.getMetaData()).getName();
        url = Objects.requireNonNull(s.getMetaData()).getUrl();
        icon = s.getMetaData().getIcons().isEmpty() ? null : s.getMetaData().getIcons().get(0);
        sessionId = s.getTopic();
        localSessionId = s.getTopic();
        settled = true;
//        extractChainsAndAddress(s.getAccounts());
//        methods.addAll(s.getNamespaces());
    }

    public WalletConnectV2SessionItem(Parcel in)
    {
        name = in.readString();
        url = in.readString();
        icon = in.readString();
        sessionId = in.readString();
        localSessionId = in.readString();
        settled = in.readInt() == 1;
        in.readStringList(chains);
        in.readStringList(wallets);
        in.readStringList(methods);
    }

    public WalletConnectV2SessionItem()
    {

    }

    public static WalletConnectV2SessionItem from(Sign.Model.SessionProposal sessionProposal)
    {
        WalletConnectV2SessionItem item = new WalletConnectV2SessionItem();
        item.name = sessionProposal.getName();
        item.url = sessionProposal.getUrl();
        item.icon = sessionProposal.getIcons().isEmpty() ? null : sessionProposal.getIcons().get(0).toString();
        item.sessionId = sessionProposal.getProposerPublicKey();
        item.settled = false;
        NamespaceParser namespaceParser = new NamespaceParser(sessionProposal.getRequiredNamespaces());
        item.chains.addAll(namespaceParser.getChains());
        item.methods.addAll(namespaceParser.getMethods());
        return item;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(icon);
        dest.writeString(sessionId);
        dest.writeString(localSessionId);
        dest.writeInt(settled ? 1 : 0);
        dest.writeStringList(chains);
        dest.writeStringList(wallets);
        dest.writeStringList(methods);
    }

    public static final Parcelable.Creator<WalletConnectV2SessionItem> CREATOR
            = new Parcelable.Creator<>()
    {
        public WalletConnectV2SessionItem createFromParcel(Parcel in)
        {
            return new WalletConnectV2SessionItem(in);
        }

        @Override
        public WalletConnectV2SessionItem[] newArray(int size)
        {
            return new WalletConnectV2SessionItem[0];
        }
    };
}
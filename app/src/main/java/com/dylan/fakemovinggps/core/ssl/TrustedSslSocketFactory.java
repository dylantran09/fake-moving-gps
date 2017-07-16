package com.dylan.fakemovinggps.core.ssl;

import android.content.Context;

import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustedSslSocketFactory implements SslManagerInterface {

    private SsX509TrustManager manager;
    private String keyStoreType;

    public TrustedSslSocketFactory(Context context, String keyStoreType, int keyStoreId, String password) {
        try {
            this.keyStoreType = keyStoreType;
            InputStream in = context.getResources().openRawResource(keyStoreId);
            try {
                manager = new SsX509TrustManager(keyStoreType, in, password);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        try {
            TrustManager[] managers = new TrustManager[1];
            SSLContext sslContext = SSLContext.getInstance(keyStoreType);
            managers[0] = manager;
            sslContext.init(null, managers, null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public X509TrustManager getTrustManager() {
        return manager;
    }
}

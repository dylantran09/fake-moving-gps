package com.dylan.fakemovinggps.core.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Tyrael
 * @version 1.0 <br>
 * @since January 2014
 */

class SsX509TrustManager implements X509TrustManager {
    private final ArrayList<X509TrustManager> mX509TrustManagers = new ArrayList<>();

    SsX509TrustManager(String keyStoreType, InputStream keyStore, String keyStorePassword)
            throws GeneralSecurityException {
        // first add original trust manager
        final TrustManagerFactory originalFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        originalFactory.init((KeyStore) null);
        for (TrustManager tm : originalFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                mX509TrustManagers.add((X509TrustManager) tm);
            }
        }

        // them add our custom trust manager
        X509TrustManager mX509TrustManagerCustom = fetchTrustManager(keyStoreType, keyStore,
                keyStorePassword);
        if (mX509TrustManagerCustom != null) {
            mX509TrustManagers.add(mX509TrustManagerCustom);
        } else {
            throw new IllegalArgumentException(
                    "Keystore is valid but cannot find TrustManagerFactory of type X509TrustManager.");
        }
    }

    private X509TrustManager fetchTrustManager(
            String keyStoreType, InputStream keyStore, String keyStorePassword)
            throws GeneralSecurityException {
        X509TrustManager ret = null;

        TrustManagerFactory tmf = prepareTrustManagerFactory(keyStoreType, keyStore,
                keyStorePassword);

        for (TrustManager manager : tmf.getTrustManagers()) {
            if (manager instanceof X509TrustManager) {
                ret = (X509TrustManager) manager;
                // break;
            }
        }

        return ret;
    }

    private TrustManagerFactory prepareTrustManagerFactory(
            String keyStoreType, InputStream keyStore, String keyStorePassword)
            throws GeneralSecurityException {
        TrustManagerFactory ret;

        KeyStore ks;
        ks = KeyStore.getInstance(keyStoreType);
        try {
            ks.load(keyStore, keyStorePassword.toCharArray());
        } catch (IOException e) {
            throw new GeneralSecurityException(
                    "Problem reading keystore stream", e);
        }
        ret = TrustManagerFactory.getInstance("X509");
        ret.init(ks);

        return ret;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Oh, I am easy!
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        boolean ok = false;
        for (X509TrustManager tm : mX509TrustManagers) {
            try {
                tm.checkServerTrusted(chain, authType);
                ok = true;
                break;
            } catch (CertificateException e) {
                // ignore
            }
        }
        if (!ok) {
            throw new CertificateException();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final ArrayList<X509Certificate> list = new ArrayList<>();
        for (X509TrustManager tm : mX509TrustManagers)
            list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
        return list.toArray(new X509Certificate[list.size()]);
    }

}

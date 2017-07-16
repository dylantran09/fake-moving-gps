package com.dylan.fakemovinggps.core.ssl;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public interface SslManagerInterface {

    SSLSocketFactory getSSLSocketFactory();

    X509TrustManager getTrustManager();
}

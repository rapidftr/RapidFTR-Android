package com.rapidftr.utils.http;

import com.google.common.collect.ObjectArrays;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

public class SelfSignedSSLSocketFactory extends SSLSocketFactory {

    protected final javax.net.ssl.SSLSocketFactory socketFactory;

    public SelfSignedSSLSocketFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(null, null, null, null, null, null);
        this.setHostnameVerifier(BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        TrustManagerFactory originalCAs = TrustManagerFactory.getInstance("X509");
        originalCAs.init((KeyStore) null);

        TrustManagerFactory customCAs = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        customCAs.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, ObjectArrays.concat(customCAs.getTrustManagers(), originalCAs.getTrustManagers(), TrustManager.class), null);
        socketFactory = sslContext.getSocketFactory();
    }

    @Override
    public Socket createSocket() throws IOException {
        return socketFactory.createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return socketFactory.createSocket(socket, host, port, autoClose);
    }
}

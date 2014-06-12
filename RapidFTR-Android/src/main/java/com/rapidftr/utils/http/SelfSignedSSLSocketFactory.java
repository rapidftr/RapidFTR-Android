package com.rapidftr.utils.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SelfSignedSSLSocketFactory extends SSLSocketFactory {

    protected final javax.net.ssl.SSLSocketFactory socketFactory;

    public SelfSignedSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(null, null, null, null, null, null);
        this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);

        TrustManager customTrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ customTrustManager }, new SecureRandom());
        socketFactory = sslContext.getSocketFactory();
    }

    @Override
    public Socket createSocket() throws IOException {
        return socketFactory.createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return socketFactory.createSocket(socket, host, port, autoClose);
    }


}

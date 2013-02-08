package com.rapidftr.utils.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SelfSignedSSLSocketFactory extends SSLSocketFactory {

    protected final javax.net.ssl.SSLSocketFactory socketFactory;

    public SelfSignedSSLSocketFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(null, null, null, null, null, null);
        this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);

        TrustManagerFactory originalCAs = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        originalCAs.init((KeyStore) null);

        TrustManagerFactory customCAs = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        customCAs.init(keyStore);

	    final X509TrustManager defaultTrustManager = (X509TrustManager) originalCAs.getTrustManagers()[0];
	    final X509TrustManager localTrustManager   = (X509TrustManager) customCAs.getTrustManagers()[0];

	    TrustManager customTrustManager = new X509TrustManager() {
		    @Override
		    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			    try {
				    localTrustManager.checkClientTrusted(x509Certificates, s);
			    } catch (Exception e) {
				    defaultTrustManager.checkClientTrusted(x509Certificates, s);
			    }
		    }

		    @Override
		    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			    try {
				    localTrustManager.checkServerTrusted(x509Certificates, s);
			    } catch (Exception e) {
				    defaultTrustManager.checkServerTrusted(x509Certificates, s);
			    }
		    }

		    @Override
		    public X509Certificate[] getAcceptedIssuers() {
			    return defaultTrustManager.getAcceptedIssuers();
		    }
	    };

	    SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ customTrustManager }, null);
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

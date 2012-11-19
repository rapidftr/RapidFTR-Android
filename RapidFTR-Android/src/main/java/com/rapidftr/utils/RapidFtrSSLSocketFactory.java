package com.rapidftr.utils;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;

import static org.apache.http.params.HttpConnectionParams.getConnectionTimeout;
import static org.apache.http.params.HttpConnectionParams.getSoTimeout;

public class RapidFtrSSLSocketFactory implements LayeredSocketFactory {

	private SSLContext sslcontext = null;

    public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
		SSLSocket sslSocket = (SSLSocket) ((socket != null) ? socket : createSocket());

		if ((localAddress != null) || (localPort > 0)) {
			// we need to bind explicitly
			if (localPort < 0) {
				localPort = 0; // indicates "any"
			}
            sslSocket.bind(new InetSocketAddress(localAddress, localPort));
		}

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
		sslSocket.connect(remoteAddress, getConnectionTimeout(params));
		sslSocket.setSoTimeout(getSoTimeout(params));
		return sslSocket;
	}

	public Socket createSocket() throws IOException {
		return getSSLContext().getSocketFactory().createSocket();
	}

    public boolean isSecure(Socket socket){
		return true;
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}

    private SSLContext getSSLContext() throws IOException{
        if (this.sslcontext == null) {
            this.sslcontext = createSslContext();
        }
        return this.sslcontext;
    }

    private static SSLContext createSslContext() throws IOException {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            @Cleanup InputStream in = RapidFtrApplication.getInstance().getResources().openRawResource(R.raw.truststore);
            trusted.load(in, "rapidftr".toCharArray());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { new RapidFtrTrustManager(trusted) }, null);
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(RapidFtrSSLSocketFactory.class));
    }

    public int hashCode() {
        return RapidFtrSSLSocketFactory.class.hashCode();
    }

}
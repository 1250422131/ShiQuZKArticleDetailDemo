package com.imcys.shiquzkarticledetaildemo.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsUtils {


    public static X509TrustManager UnSafeTrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
    public static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return false;
        }
    };

    public static SSLParams getSslSocketFactory() {
        return getSslSocketFactoryBase(null, null, null);
    }

    private static SSLParams getSslSocketFactoryBase(X509TrustManager trustManager, InputStream bksFile, String password, InputStream... certificates) {
        SSLParams sSLParams = new SSLParams();
        try {
            KeyManager[] prepareKeyManager = prepareKeyManager(bksFile, password);
            TrustManager[] prepareTrustManager = prepareTrustManager(certificates);
            if (trustManager == null) {
                if (prepareTrustManager != null) {
                    trustManager = chooseTrustManager(prepareTrustManager);
                } else {
                    trustManager = UnSafeTrustManager;
                }
            }
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(prepareKeyManager, new TrustManager[]{trustManager}, null);
            sSLParams.sSLSocketFactory = sSLContext.getSocketFactory();
            sSLParams.trustManager = trustManager;
            return sSLParams;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        if (bksFile != null && password != null) {
            try {
                KeyStore keyStore = KeyStore.getInstance("BKS");
                keyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, password.toCharArray());
                return keyManagerFactory.getKeyManagers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates != null && certificates.length > 0) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                int length = certificates.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    InputStream inputStream = certificates[i];
                    int i3 = i2 + 1;
                    keyStore.setCertificateEntry(Integer.toString(i2), certificateFactory.generateCertificate(inputStream));
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                    i2 = i3;
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                return trustManagerFactory.getTrustManagers();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }
}
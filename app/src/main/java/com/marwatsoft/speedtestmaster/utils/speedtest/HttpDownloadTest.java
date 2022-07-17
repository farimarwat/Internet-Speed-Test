package com.marwatsoft.speedtestmaster.utils.speedtest;

import android.util.Log;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

/**
 * @author erdigurbuz
 */
public class HttpDownloadTest extends Thread {
    public DownloadCallBacks mCallbacks;
    public interface DownloadCallBacks{
        void onStart();
        void onProgress(Double downloadrate);
        void onFinished(Double downloadrate);
        void onError(int responseCode, String responseMessage);
    }
    public String fileURL = "";
    long startTime = 0;
    long endTime = 0;
    double downloadElapsedTime = 0;
    int downloadedByte = 0;
    double finalDownloadRate = 0.0;
    boolean finished = false;
    double instantDownloadRate = 0;
    int timeout = 20;
    boolean mIsStopped = false;

    HttpsURLConnection httpsConn = null;
    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };
    public HttpDownloadTest(String fileURL, DownloadCallBacks callBacks) {
        this.mCallbacks = callBacks;
        this.fileURL = fileURL;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd;
        try {
            bd = new BigDecimal(value);
        } catch (Exception ex) {
            return 0.0;
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getInstantDownloadRate() {
        return instantDownloadRate;
    }

    public void setInstantDownloadRate(int downloadedByte, double elapsedTime) {

        if (downloadedByte >= 0) {
            this.instantDownloadRate = round((Double) (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime), 2);
        } else {
            this.instantDownloadRate = 0.0;
        }
    }

    public double getFinalDownloadRate() {
        return round(finalDownloadRate, 2);
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        mIsStopped = false;
        if(mCallbacks != null){
            mCallbacks.onStart();
        }
        URL url = null;
        downloadedByte = 0;
        int responseCode = 0;

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(fileURL + "random4000x4000.jpg");
        fileUrls.add(fileURL + "random3000x3000.jpg");
        fileUrls.add(fileURL + "random4000x4000.jpg");
        fileUrls.add(fileURL + "random3000x3000.jpg");

        startTime = System.currentTimeMillis();

        outer:
        for (String link : fileUrls) {
            try {
                url = new URL(link);
                httpsConn = (HttpsURLConnection) url.openConnection();
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                httpsConn.setSSLSocketFactory(sc.getSocketFactory());
                httpsConn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                httpsConn.connect();
                responseCode = httpsConn.getResponseCode();
            } catch (Exception ex) {
                Timber.e("HttpDownloadTest: "+ex.getMessage());
                if(mCallbacks != null){
                    mCallbacks.onError(responseCode,ex.toString());
                }
                break outer;
            }

            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    byte[] buffer = new byte[10240];
                    InputStream inputStream = httpsConn.getInputStream();
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        downloadedByte += len;
                        endTime = System.currentTimeMillis();
                        downloadElapsedTime = (endTime - startTime) / 1000.0;
                        setInstantDownloadRate(downloadedByte, downloadElapsedTime);
                        mCallbacks.onProgress(getInstantDownloadRate());
                        if (downloadElapsedTime >= timeout || mIsStopped) {
                            break outer;
                        }
                    }

                    inputStream.close();
                    httpsConn.disconnect();
                } else {
                    Timber.e("Link Not Found");
                   if(mCallbacks != null){
                       mCallbacks.onError(httpsConn.getResponseCode(),httpsConn.getResponseMessage());
                   }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        endTime = System.currentTimeMillis();
        downloadElapsedTime = (endTime - startTime) / 1000.0;
        finalDownloadRate = ((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime;
        finished = true;
        if(finished && mCallbacks != null){
            mCallbacks.onFinished(finalDownloadRate);
        }
    }

    public void unregisterCallbacks(){
        mIsStopped = true;
        this.interrupt();
        mCallbacks = null;
    }
}

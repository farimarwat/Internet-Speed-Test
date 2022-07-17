package com.marwatsoft.speedtestmaster.utils.speedtest;


import android.util.Log;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class HttpUploadTest extends Thread {
    public interface UploadCallBacks {
        void onStart();
        void onProgress(Double uploadrate);
        void onFinished(Double uploadrate);
        void onError(String error);
    }
    public String fileURL = "";
    static int uploadedKByte = 0;
    double uploadElapsedTime = 0;
    boolean finished = false;
    double elapsedTime = 0;
    double finalUploadRate = 0.0;
    long startTime;
    public UploadCallBacks mCallbacks;
    public HandlerUpload mHandlerUpload;
    public ExecutorService executor;

    public HttpUploadTest(String fileURL, UploadCallBacks uploadCallBacks) {
        this.fileURL = fileURL;
        this.mCallbacks = uploadCallBacks;
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

    public boolean isFinished() {
        return finished;
    }

    public double getInstantUploadRate() {
        try {
            BigDecimal bd = new BigDecimal(uploadedKByte);
        } catch (Exception ex) {
            return 0.0;
        }

        if (uploadedKByte >= 0) {
            long now = System.currentTimeMillis();
            elapsedTime = (now - startTime) / 1000.0;
            double instantupload = round((Double) (((uploadedKByte / 1000.0) * 8) / elapsedTime), 2);
            return instantupload;
        } else {
            return 0.0;
        }
    }

    public double getFinalUploadRate() {
        return round(finalUploadRate, 2);
    }

    @Override
    public void run() {
        try {
            if(mCallbacks != null){
                mCallbacks.onStart();
            }
            URL url = new URL(fileURL);
            uploadedKByte = 0;
            startTime = System.currentTimeMillis();
             executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < 4; i++) {
                mHandlerUpload = new HandlerUpload(url);
                executor.execute(mHandlerUpload);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(100);
                    mCallbacks.onProgress(getInstantUploadRate());
                    Log.e("TEST","Progress: "+getInstantUploadRate());
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Timber.e("Exception:"+ex.toString());
                    this.interrupt();
                }
            }

            long now = System.currentTimeMillis();
            uploadElapsedTime = (now - startTime) / 1000.0;
            finalUploadRate = (Double) (((uploadedKByte / 1000.0) * 8) / uploadElapsedTime);

        } catch (Exception ex) {
            ex.printStackTrace();
            executor.shutdown();
            mCallbacks.onError(ex.getMessage());
            this.interrupt();
        }
        if(mCallbacks != null){
            mCallbacks.onFinished(finalUploadRate);
        }
        finished = true;
    }
    public void unregistercallbacks(){
        executor.shutdown();
        this.interrupt();
        mHandlerUpload.unregistercallbacks();
    }
}

class HandlerUpload extends Thread {

    URL url;
    boolean mIsStopped = false;
    public HandlerUpload(URL url) {
        this.url = url;
    }

    public void run() {
        byte[] buffer = new byte[150 * 1024];
        long startTime = System.currentTimeMillis();
        int timeout = 12;
        mIsStopped = false;
        while (true) {
            try {
                HttpsURLConnection conn = null;
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
                conn = (HttpsURLConnection) url.openConnection();
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());
                conn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.connect();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


                dos.write(buffer, 0, buffer.length);
                dos.flush();

                conn.getResponseCode();

                HttpUploadTest.uploadedKByte += buffer.length / 1024.0;
                long endTime = System.currentTimeMillis();
                double uploadElapsedTime = (endTime - startTime) / 1000.0;
                if (uploadElapsedTime >= timeout || mIsStopped) {
                    break;
                }

                dos.close();
                conn.disconnect();
            } catch (Exception ex) {
                Timber.e(ex);
                break;
            }
        }
    }
    public void unregistercallbacks(){
        this.interrupt();
        mIsStopped = true;
    }
}

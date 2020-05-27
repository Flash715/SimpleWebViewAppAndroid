package com.amikom.mywebview;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;





public class MainActivity extends AppCompatActivity {

    WebView webView;
    WebSettings websettingku;
    SwipeRefreshLayout swipeLayout;



    private boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case
                    KeyEvent.KEYCODE_BACK:
                        if(webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                        return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isConnected())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Aplikasi ini menggunakan koneksi internet")
                    .setMessage("Tolong cek koneksi internet anda !")
                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

        else
        {
            Toast.makeText(MainActivity.this,"Welcome", Toast.LENGTH_LONG).show();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,1 );
            }
        }

        webView = (WebView)findViewById(R.id.WebView1);
        webView.loadUrl("http://student.amikompurwokerto.ac.id/");
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        webView.setWebViewClient(new WebViewClient(){
            public void onReceivedErrors(final WebView webView, int errorCode, String description, String failingUrl){
                try{
                    webView.stopLoading();
                } catch (Exception e){
                }
                if (webView.canGoBack()){
                    webView.goBack();
                }
                webView.loadUrl("about:blank");
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Check your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Reload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                {
                    alertDialog.show();
                    super.onReceivedError(webView, errorCode, description, failingUrl);
                }


                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK){
                            finish();
                        }
                        return true;
                    }
                });
            }
            public void onPageFinished(WebView webView,String Url){
                swipeLayout.setRefreshing(false);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView,int newProgress) {
                if (webView.getProgress() == 100) {
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(true);
                }
            }
        });




        swipeLayout.setRefreshing(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        webView.loadUrl("http://student.amikompurwokerto.ac.id/");
                    }
                },1000);
            }
        });





        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                myRequest.addRequestHeader("cookie",cookies);
                myRequest.addRequestHeader("User-Agent", userAgent);
                myRequest.setDescription("Download File");
                myRequest.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


                myRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(myRequest);


                DownloadManager myManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this, "File sedang di Download !!!", Toast.LENGTH_SHORT).show();
            }
        });



        /*websettingku = webviewku.getSettings();

        webviewku.setWebViewClient(new WebViewClient());

        webviewku.loadUrl("http://student.amikompurwokerto.ac.id/");
            //writed by ivan febriansyah...

         */









    }

    }


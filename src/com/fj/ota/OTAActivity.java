package com.fj.ota;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fj.ota.R;

public class OTAActivity extends Activity {
	
	WebView mWebView;
	final Activity activity = this;
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.webview);

	    final ProgressDialog progressDialog = new ProgressDialog(activity);
	    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    progressDialog.setCancelable(false);

	    // This incremental info needs to be changed to coincide with ROM developer incremental info
	    // This will be this line in your build.prop ro.build.version.incremental
	    String RomInc= android.os.Build.VERSION.INCREMENTAL;

	    // Your incremental date will start with the numerical date in ro.build.version.incremental
	    // 13,21 here will be the character date range and needs to be changed to match yours
	    // Example: Mine is eng.ctindall.20121026.175415 Date starts after character 13 and ends with 21
	    String BuildDate= RomInc.substring(13, 21);	    

	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);

	    // This URL needs to be changed to your own ROM url found in your OTA
        String url = "http://www.jdvhosting.com/OTA2/ota.php?ROMID=47&ID=44950871&BuildDate=" + BuildDate;
        mWebView.loadUrl(url);
	    mWebView.setWebViewClient(new OTAWebViewClient() {
	    	@Override
		    public void onPageFinished(WebView view, String url) {
		    super.onPageFinished(view, url);
		    }
		    });
	    mWebView.setDownloadListener(new DownloadListener() {
	        public void onDownloadStart(String url, String userAgent,
	                String contentDisposition, String mimetype,
	                long contentLength) {
	            Intent intent = new Intent(Intent.ACTION_VIEW);
	            intent.setData(Uri.parse(url));
	            startActivity(intent);

	        }
	    });

	    mWebView.setWebChromeClient(new WebChromeClient() {
	        public void onProgressChanged(WebView view, int progress) {
	            progressDialog.show();
	            progressDialog.setProgress(0);
	            activity.setProgress(progress * 1000);

	            progressDialog.incrementProgressBy(progress);

	            if(progress == 100 && progressDialog.isShowing())
	                progressDialog.dismiss();
	        }
	    });
	}
	
	private class OTAWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	    
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
        
    }
    
}

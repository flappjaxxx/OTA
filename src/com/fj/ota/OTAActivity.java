package com.fj.ota;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	    String BuildDate = "29991231";
	    
	    int strPos = RomInc.indexOf("2012");
	    if (strPos >= 0)
	    {	
	    	BuildDate=RomInc.substring(strPos, strPos+8); 
	    }
	    else  //2012 not found, try for 2013
	    {
	    	strPos = RomInc.indexOf("2013");
	    	if (strPos >= 0)
		    {	
		    	BuildDate=RomInc.substring(strPos, strPos+8); 
		    }
	    	else // 2013 not found either, try 2014
		    {
		    	strPos = RomInc.indexOf("2014");
		    	if (strPos >= 0)
			    {	
			    	BuildDate=RomInc.substring(strPos, strPos+8); 
			    }
		    }
	    }

	    // This is now deprecated.
	    // Your incremental date will start with the numerical date in ro.build.version.incremental
	    // 13,21 here will be the character date range and needs to be changed to match yours
	    // Example: Mine is eng.ctindall.20121026.175415 Date starts after character 13 and ends with 21
	    //String BuildDate= RomInc.substring(13, 21);	    

	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);

	    // This URL needs to be changed to your own ROM url found in your OTA
        String url = exec("getprop ro.ota2.url");
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
    
    private static String exec(String command) {
        ArrayList<String> output = new ArrayList<String>();
        try {
            String line;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            reader.close();
            process.destroy();
        } catch(Exception e) {
            // FIXME: log
        }
        if (output.isEmpty()) {
            return "";
        } else {
            return output.get(output.size()-1);
        }
    }
}

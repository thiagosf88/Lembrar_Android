package br.thg.lmb.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.thg.lmb.AppLembrar;

public class TestWebView extends WebViewClient {
	
	AppLembrar actRef;
	boolean timeout = true;
	public TestWebView(AppLembrar wva){
		actRef = wva;
	}
/*
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
        running++;
        view.loadUrl(urlNewString);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        running = Math.max(running, 1); // First request move it to 1.
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(--running == 0) { // just "running--;" if you add a timer.
        	System.out.println(url + " - loaded");
        	Intent mIntent = new Intent();
			mIntent.putExtra(WebOperationActivity.ISTHELASTPAGE, actRef.isTheLastPage());
			actRef.setResult(Activity.RESULT_OK, mIntent);
			actRef.finish();
        }
    }*/
	
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    	new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(timeout) {
                    gettingOut("Timeout!!"); 
                }
            }
        }).start();
    }
	
	public void onPageFinished(WebView view, String url) {
		timeout = false;
		gettingOut(url + " loaded!");
	}
	
	private void gettingOut(String message){
		System.out.println(message);
		Intent mIntent = new Intent();
		//mIntent.putExtra("");
		actRef.setResult(Activity.RESULT_OK, mIntent);
		
	}
	
}
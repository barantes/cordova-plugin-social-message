//
//  SocialMessage.java
//  Copyright (c) 2013 Lee Crossley - http://ilee.co.uk
//

package uk.co.ilee.socialmessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

@SuppressLint("DefaultLocale")
public class SocialMessage extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		JSONObject json = args.getJSONObject(0);
		
		if("openFacebook".equalsIgnoreCase(action)) {
			String facebookId = getJSONProperty(json, "facebookId");
			try {
				doOpenFacebookIntent(facebookId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			String text = getJSONProperty(json, "text");
			String subject = getJSONProperty(json, "subject");
			String url = getJSONProperty(json, "url");
			String image = getJSONProperty(json, "image");
			if (url != null && url.length() > 0) {
				if (text == null) {
					text = url;
				} else {
					text = text + " " + url;
				}
			}
			try {
				doSendIntent(text, subject, image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return true;
	}
	
	private String getJSONProperty(JSONObject json, String property) throws JSONException {
		if (json.has(property)) {
			return json.getString(property);
		}
		return null;
	}

	private void doSendIntent(String text, String subject, String image) throws IOException {
		final Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		if (text != null && text.length() > 0) {
			sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		}
		if (subject != null && subject.length() > 0) {
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		}
		if (image != null && image.length() > 0) {
			sendIntent.setType("image/*");
			final URL url = new URL(image);
			String storageDir = Environment.getExternalStorageDirectory().getPath();
			final String path = storageDir + "/" + image.substring(image.lastIndexOf("/") + 1, image.length());
			cordova.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						saveImage(url, path);
						sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
						cordova.getActivity().startActivityForResult(sendIntent, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			sendIntent.setType("text/plain");
			cordova.startActivityForResult(this, sendIntent, 0);
		}
	}
	
	public static void saveImage(URL url, String outputPath) throws IOException {
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(outputPath);
		byte[] b = new byte[2048];
		int length;
		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		is.close();
		os.close();
	}
	
	private void doOpenFacebookIntent(final String facebookId) throws IOException {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					cordova.getActivity().startActivityForResult(getOpenFacebookIntent(facebookId), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public Intent getOpenFacebookIntent(String facebookId) {
	    try {
	    	cordova.getActivity().getPackageManager()
	                .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
	        return new Intent(Intent.ACTION_VIEW,
	                Uri.parse("fb://profile/" + facebookId));
	    } catch (Exception e) {
	        return new Intent(Intent.ACTION_VIEW,
	                Uri.parse("https://www.facebook.com/" + facebookId));
	    }
	}
	
}

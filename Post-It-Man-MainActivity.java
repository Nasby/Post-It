package com.example.post_it_man;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
//--------twitter imports----------------------

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
//---------------------------------------------
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.FbDialog;
import android.widget.ImageButton;

import android.widget.ToggleButton;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements OnClickListener{
	
	Facebook fb;
	ImageView pic, button;
	ToggleButton facebook_tb;
	ToggleButton twitter_tb;
	SharedPreferences sp;
	private ImageButton shareButton;
    private static final String Post = "xxxxxxxxxxxxx";
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    private EditText yourEditText;
    private String toShare;
    
    //----------twitter-----------------------------------------------------------------------
    static String TWITTER_CONSUMER_KEY = "W8VbyuISfsx15SjMeSftGg";
    static String TWITTER_CONSUMER_SECRET = "GOnEqi86FjYV7KiElcHdMStPj4bRloWuVPwOvDrU0h0";
 
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
 
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
 // Progress dialog
    ProgressDialog pDialog;
 
    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
   //------------------------------------------------------------------------------------------ 
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
          }
        
       mFacebook = new Facebook(Post);
        mAsyncRunner = new AsyncFacebookRunner(mFacebook);
        
//----------------POST BUTTON-----------------------------------------------
        shareButton = (ImageButton) findViewById(R.id.angry_btn);
        
//------Calling Twitter ToggleButton----------------------------------------------------
        twitter_tb =(ToggleButton) findViewById(R.id.twitter_tb);
        facebook_tb=(ToggleButton) findViewById(R.id.facebook_tb);
        
        // Shared Preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);
        
        twitter_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	  if (!isTwitterLoggedInAlready())
        			      {loginToTwitter();}
                	  else {
                          // user already logged into twitter
                          Toast.makeText(getApplicationContext(),
                                  "Already Logged into twitter", Toast.LENGTH_SHORT).show();
                      }
                } 
            }
        });
        
        
        facebook_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
         	
                	 Session session = Session.getActiveSession();
                     
                     facebook_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                     {
                     	 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                     	 {
                     		 if(isChecked)
                     		 {
                     		   yourEditText = (EditText) findViewById(R.id.editText1);
                     		   toShare = yourEditText.getText().toString();
                     		   String APP_ID = "1427323920822672";
                     		   final Facebook fb = new Facebook(APP_ID);
                     		   sp = getPreferences(MODE_PRIVATE);
                     		   String access_token = sp.getString("access_token", null);
                     		   long expire = sp.getLong("access_expires", 0);
                     		   if ("access_token" != null)
                     		   {
                 		        	fb.setAccessToken(access_token);
                 		        	if (expire != 0)
                 		        	{
                 		        		fb.setAccessExpires(expire);
                 		        	}
                 		        		
                 		        	
                 		        }
                 			 if(!fb.isSessionValid())
                 			 {
                 				 fb.authorize(MainActivity.this, new String [] {"email" , "publish_stream"}, new DialogListener()
                 				 {
                 					 
                 					 @Override
                 						public void onFacebookError(FacebookError e) {
                 							// TODO Auto-generated method stub
                 							Toast.makeText(MainActivity.this, "fbError",Toast.LENGTH_SHORT).show();
                 						}
                 						
                 						@Override
                 						public void onError(DialogError e) {
                 							// TODO Auto-generated method stub
                 							Toast.makeText(MainActivity.this, "onError",Toast.LENGTH_SHORT).show();
                 							
                 						}
                 						
                 						@Override
                 						public void onComplete(Bundle values) {
                 							// TODO Auto-generated method stub
                 							SharedPreferences.Editor editor = sp.edit();
                 							editor.putString("access_token",fb.getAccessToken());
                 							editor.putLong("access_token", fb.getAccessExpires());
                 							editor.commit();
                 							
                 						}
                 						
                 						@Override
                 						public void onCancel() {
                 							// TODO Auto-generated method stub
                 							Toast.makeText(MainActivity.this, "onCancel",Toast.LENGTH_SHORT).show();
                 						}
                 				 });
                     			 }
                 			 
                 			 else{
                 			Toast.makeText(getApplicationContext(),
                             "Already Logged into facebook ", Toast.LENGTH_SHORT)
                             .show();}
                     		 }

                     	 }
                     });//onclick
    	
                	
                } 
            }
        });
        
        /** This if conditions is tested once is
         * redirected from twitter page. Parse the uri to get oAuth
         * Verifier
         * */
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
 
                try {
                    // Get the access token
                    AccessToken accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);
 
                    // Shared Preferences
                    Editor e = mSharedPreferences.edit();
 
                    // After getting access token, access token secret
                    // store them in application preferences
                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    e.putString(PREF_KEY_OAUTH_SECRET,
                            accessToken.getTokenSecret());
                    // Store login status - true
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit(); // save changes
 
                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
       
                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        }
        
        
//---------------------------------------------------------------------------------------        
       
        
        yourEditText = (EditText) findViewById(R.id.editText1);
        
        String APP_ID = "1427323920822672";
    

        shareButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // Call update status function
                // Get the status from EditText
            	toShare = yourEditText.getText().toString();
           
                // Check for blank text
                if (toShare.trim().length() > 0) {
                    // update status
                    new updateTwitterStatus().execute(toShare);
                    
                    shareonFacebook(v);
                   
                 
                } else {
                    // EditText is empty
                    Toast.makeText(getApplicationContext(),
                            "Please enter status message", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        
        
        
        
        
        
     
 
    }
    
 //---------------------------twitter-----------------------------------------------------------
    /**
     * Function to login twitter
     * */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
           ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();
             
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
 
            try {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
               this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            // user already logged into twitter
            Toast.makeText(getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_SHORT).show();
        }
    }
 
    class updateTwitterStatus extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Updating post...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                 
                // Access Token 
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
                 
                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                 
                // Update status
                twitter4j.Status response = twitter.updateStatus(status);
                 
                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status posted successfully", Toast.LENGTH_SHORT)
                            .show();
                    // Clearing EditText field
                    yourEditText.setText("");
                }
            });
        }
 
    }
    
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
//---------------------------------------------------------------------------------------------
    @SuppressWarnings("deprecation")
	public void shareonFacebook(View V)
    {
    	 Bundle params = new Bundle();
         params.putString("message", toShare);
         RequestListener ll =  new RequestListener()   
         {
             @SuppressWarnings("unused")
			public void onMalformedURLException(MalformedURLException e) {}
             @SuppressWarnings("unused")
			public void onIOException(IOException e) {}
             @SuppressWarnings("unused")
			public void onFileNotFoundException(FileNotFoundException e) {}
             @SuppressWarnings("unused")
			public void onFacebookError(FacebookError e) {}
             @SuppressWarnings("unused")
			public void onComplete(String response) {}
			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				
			}
         };
         mAsyncRunner.request("me/feed", params, "POST",null, ll );
    }
   
	@SuppressWarnings("deprecation")
	@Override
    public void onClick(View V)
    {
    	if(fb.isSessionValid())
    	{
    		try{
    		fb.logout(getApplicationContext());
    		//updateButtonImage();
    		}
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		fb.authorize(MainActivity.this, new String[] {"email"},new DialogListener() {
				
				@Override
				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "fbError",Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onError(DialogError e) {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "onError",Toast.LENGTH_SHORT).show();
					
				}
				
				@Override
				public void onComplete(Bundle values) {
					// TODO Auto-generated method stub
					Editor editor = sp.edit();
					editor.putString("access_token", fb.getAccessToken());
					editor.putLong("access_token", fb.getAccessExpires());
					editor.commit();
					//updateButtonImage();
					
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "onCancel",Toast.LENGTH_SHORT).show();
				}
			});
    	}
    }

   

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @SuppressWarnings("deprecation")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	fb.authorizeCallback(requestCode, resultCode, data);
    	
    }
}

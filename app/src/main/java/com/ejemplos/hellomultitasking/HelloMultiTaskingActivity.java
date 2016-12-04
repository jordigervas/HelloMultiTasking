package com.ejemplos.hellomultitasking;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelloMultiTaskingActivity extends Activity {

	static final int DIALOG_EXIT_ID = 0;
	
	private Handler handler;
	private ProgressBar progress;
	private ProgressDialog progressDialog;
	private TextView textView;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		handler = new Handler();
		textView = (TextView) findViewById(R.id.TextView01);
		
		// OnClick Event Callback for QuitButton
        final Button QuitButton = (Button) findViewById(R.id.quitbutton);
        QuitButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	showDialog(DIALOG_EXIT_ID);
        	
            	// Start progress dialog using a java thread (Not Correct).
            	new Thread(new Runnable() {
                    public void run() {
                    	int x=0;
                    	while (x<100)
                    	{
	                    	progressDialog.incrementProgressBy(1);
	                    	x++;
	                    	try {
	                    		Thread.sleep(100);
		                    } catch (InterruptedException e) {
								e.printStackTrace();
							}		            
                    	}
                    	finish();
                    }
                }).start();
            }
        });
	}
	
	protected Dialog onCreateDialog(int id) 
	{

	    switch(id) {
		    case DIALOG_EXIT_ID:
		        // do the work to define the exit Dialog
		    	progressDialog = new ProgressDialog(HelloMultiTaskingActivity.this);
		    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    	progressDialog.setMessage("Closing Application...");
		    	progressDialog.setCancelable(false);
		    	return progressDialog;

		    default:
		        return null;
	    }
	}

	// Handler for track the progression.
	public void startProgress(View view) {
		// Do something long
		Runnable runnable = new Runnable() {
			public void run() {
				for (int i = 0; i <= 10; i++) {
					final int value = i;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						public void run() {
							progress.setProgress(value);
						} 
					});
				}
			} 
		};
		new Thread(runnable).start();
	}
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			textView.setText(result);
		}
	}

	public void readWebpage(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] {"http://www.eps.udl.cat"});
	}
	
}
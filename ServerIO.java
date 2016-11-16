package com.example.app;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains a way to retrieve data asynchronously from e.g. a JSON server
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
<a href="http://www.wtfpl.net/">
  <img
    src="http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png"
    width="80"
    height="15"
    alt="WTFPL"
    />
</a>
 */
public class ServerIO extends AsyncTask<Void, Void, String>
{
  /** ReST server to connect to */
  public static final String SERVER = "http://akesohackerspace.com/";

  /** Timeout in miliseconds before giving up on a connection */
  public static final int TIMEOUT = 10000;

  /** Listener to be notified when this task completes */
  private ServerIOListener listener;

  /**
   * Fetch some example data
   *
   * @param listener caller to be notified when the data is fetched
   */
  public static void getSampleData(ServerIOListener listener)
  {
    ServerIO task = new ServerIO();
    task.listener = listener;
    task.execute();
  }

  /**
   * Perform the request ReST action
   *
   * @param params requested action and arguments
   * @return (e.g. JSON) result
   */
  @Override
  protected String doInBackground(Void... params)
  {
    HttpURLConnection connection;
    BufferedInputStream bis;
    URL url;
    HttpURLConnection con = null;
    int responseCode = 500;
    BufferedReader reader;
    String line;
    StringBuilder sb = new StringBuilder();

    // try to connect
    try
    {
      url = new URL(listener.uri());
      con = (HttpURLConnection) url.openConnection();
      con.setConnectTimeout(TIMEOUT);
      con.setReadTimeout(TIMEOUT);
      responseCode = con.getResponseCode();

      // connected ok, get response
      if ( (200 <= responseCode) && (responseCode <= 299) )
      {
        bis = new java.io.BufferedInputStream(con.getInputStream());
        reader = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

        while ((line = reader.readLine()) != null)
        {
          sb.append(line);
        }

        bis.close();
      }
    }

    // url not instantiated
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }

    // con not instantiated
    catch (IOException e)
    {
      e.printStackTrace();
    }
    if (null == con)
    {
      throw new NullPointerException("con not instantiated");
    }

    return sb.toString();
  }

  /**
   * Handle the result of the doInBackground call
   *
   * @param result data retrieved from the ReST server
   */
  @Override
  protected void onPostExecute(String result) {
    listener.onRetrieved(result);
  }

  /**
   * A listener to be notified when data has been retrieved
   */
  public interface ServerIOListener
  {
    /** @return URI of the function to call */
    String uri();

    /**
     * Handle the result of the doInBackground call
     *
     * @param result data retrieved from the ReST server
     */
    void onRetrieved(String result);
  }
}

package com.example.app;

import android.location.Geocoder;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Semaphore;

/**
 * Backward compatible geocoding API that does not rely on Google play etc.
 *
 * Note:
 * You will need your own API key for this to work.
 * Please visit here for more info :
 * https://developers.google.com/maps/documentation/geocoding/get-api-key
 * Make sure to enable the "Google Maps Geocoding API"
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
 */
public class WebGeocoder
  implements
    ServerIO.ServerIOListener
{
  /** output */
  public ResultSet output;

  /** Street address to look up (required) */
  private String lookup;

  /** Location of the maps API */
  private static final String SERVER = "maps.googleapis.com";
  private static final String PROTO = "https";
  private static final String PAGE = "maps/api/geocode";
  private static final String OUTPUT_FORMAT = "json";
  private static final String KEY = "YOUR API KEY GOES HERE";

  /** URI encode version that does not rely on charset availability */
  public static String URLEncode(String input)
  {
    return input
          .replaceAll("[^\\x20-\\x7E]", "")
          .replace("+", "%2B").replace(" ", "+")
          .replace("!", "%21").replace("\"", "%22").replace("#", "%23").replace("$",  "%24")
          .replace("%", "%25").replace("&",  "%26").replace("'", "%27").replace("(",  "%28")
          .replace(")", "%29").replace("*",  "%2A")                    .replace(",", "%2C")
          .replace("-", "%2D").replace(".",  "%2E").replace("/", "%2F")
                              .replace(":", "%3A").replace(";", "%3B").replace("<",  "%3C")
          .replace("=", "%3D").replace(">",  "%3E").replace("?", "%3F").replace("@",  "%40")
                                                   .replace("[", "%5B").replace("\\", "%5C")
          .replace("]", "%5D").replace("^",  "%5E").replace("_", "%5F").replace("`",  "%60")
                                                   .replace("{", "%7B").replace("|",  "%7C")
          .replace("}", "%5D").replace("~",  "%7E")
          ;
  }

  /**
   * Construct with all required objects
   */
  public WebGeocoder(String lookup)
  {
    try
    {
      this.lookup = URLEncoder.encode(lookup, "UTF-8");
    }
    catch (UnsupportedEncodingException ignored)
    {
      this.lookup = URLEncode(lookup);
    }
  }

  /** @return the URI of the create group method */
  @Override
  public String uri()
  {
    return String.format("%s://%s/%s/%s?address=%s&key=%s", PROTO, SERVER, PAGE, OUTPUT_FORMAT, lookup, KEY);
  }

  /** @return data to POST to the server */
  @Override
  public String postData()
  {
    return null;
  }

  /**
   * Ensure a course was created, then swap views
   *
   * @param result data retrieved from the ReST server
   */
  @Override
  public void onRetrieved(String result)
  {
    Gson formatter = new Gson();
    Semaphore mutex = new Semaphore(1);

    // critical section
    try
    {
      output = formatter.fromJson(result, ResultSet.class);
    }

    // schedule a retry if we got no reply from the server
    catch (
        NullPointerException
            | IllegalStateException
            | JsonSyntaxException
            ignored
        )
    {
      if (1 > result.length())
      {
        ServerIO.getData(this);
      }
    }
    finally
    {
      mutex.release();
    }
  }

  /** returned JSON object */
  public class AddressComponent
  {
    String long_name;
    String short_name;
    String[] types;
  }

  /** returned JSON object */
  public class MyLocation
  {
    double lat;
    double lng;
  }

  /** returned JSON object */
  public class ViewPort
  {
    MyLocation northeast;
    MyLocation southwest;
  }

  /** returned JSON object */
  public class Geometry
  {
    MyLocation location;
    ViewPort viewport;
    String location_type;
  }

  /** returned JSON object */
  public class Result
  {
    AddressComponent[] address_components;
    String formatted_address;
    Geometry geometry;
    String place_id;
    String[] types;
  }

  /** returned JSON object */
  public class ResultSet
  {
    Result[] results;
    String status;
  }
}

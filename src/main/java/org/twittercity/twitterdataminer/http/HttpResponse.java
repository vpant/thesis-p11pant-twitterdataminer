package org.twittercity.twitterdataminer.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * Data object that represents an Http Response
*/
public class HttpResponse
{
    private HttpURLConnection connection;
    private int responseCode;
    private InputStream is;
    private JSONObject json = null;
    
    public HttpResponse(HttpURLConnection connection) throws IOException
    {
        this.connection = connection;   
        try 
        {
            this.responseCode = connection.getResponseCode();
        } 
        catch (IOException e) 
        {
            /*
             * If the user has revoked the access token in use, then Twitter
             * returns a 401 with no "WWW-Authenticate" header.
             */
            if ("Received authentication challenge is null".equals(e.getMessage())) 
            {
                this.responseCode = connection.getResponseCode();
            } 
            else 
            {
                throw e;
            }
        }
        if (null == (is = connection.getErrorStream())) {
            is = connection.getInputStream();
        }
    }
    
    public JSONObject asJSONObject()
    {
        BufferedReader streamReader = null;
        if (json == null)
        {
            streamReader = new BufferedReader(new InputStreamReader(is));
            json = new JSONObject(new JSONTokener(streamReader));
        }
        if (streamReader != null) 
        {
            try 
            {
                streamReader.close();
            } 
            catch (IOException ex){ /* nothing to do */ }
        }
        disconnect();
        return json;
    }
    
    public int getResponseCode()
    {
        return responseCode;
    }
    
    public void disconnect() 
    {
        connection.disconnect();
    }
}

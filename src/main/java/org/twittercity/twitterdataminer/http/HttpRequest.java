package org.twittercity.twitterdataminer.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterException;

/**
 *
 */
public class HttpRequest implements HttpResponseCode
{
	private Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	
    private String url;
    private RequestMethod requestMethod;
    private Map<String,String> requestHeaders;
    private String postParam;
    
    
    public HttpRequest(RequestMethod requestMethod, String url, HttpParameter[] parameters, Map<String,String> requestHeaders)
    {
    	this(requestMethod, url, parameters, requestHeaders, null);
    }
    
    public HttpRequest(RequestMethod requestMethod, String url, HttpParameter[] parameters, Map<String,String> requestHeaders, String postParam)
    {
        this.url = url + ((parameters != null) ? "?" + HttpParameter.encodeParameters(parameters) : "");
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.postParam = postParam;
        logger.debug("Sending request to url: {}", this.url);
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }
    
    public Map<String,String> getRequestHeaders()
    {
        return requestHeaders;
    }
    
    public void setRequestHeaders(Map<String,String> headers)
    {
        this.requestHeaders = headers;
    }
    
    public String getPostParam()
    {
        return postParam;
    }
    
    /**
     * 
     */
    public HttpResponse handleRequest() throws TwitterException
    {
        HttpResponse httpResponse = null; 
        HttpURLConnection connection;
        OutputStream os = null;
        int responseCode = -1;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            setHeaders(connection);
            //If post we requesting bearer token
            if(getRequestMethod() == RequestMethod.POST) {
            	connection.setDoOutput(true);
                byte[] outputBytes = getPostParam().getBytes("UTF-8");
                os = connection.getOutputStream();
                os.write(outputBytes);
                os.flush();
                os.close();
            }
        
            httpResponse = new HttpResponse(connection);
            responseCode = httpResponse.getResponseCode();
            JSONObject responseString = httpResponse.asJSONObject();
            
            if (responseCode < OK || (responseCode != FOUND && MULTIPLE_CHOICES <= responseCode)) {
                if (responseCode == ENHANCE_YOUR_CLAIM || responseCode == BAD_REQUEST || 
                		responseCode <= INTERNAL_SERVER_ERROR) {
                    throw new TwitterException("The response from Twitter was not good! ", httpResponse);
                }
            } 
        } catch (MalformedURLException mue) {
            throw new TwitterException(mue.getMessage());
        } catch (IOException ioe) {
        	throw new TwitterException(ioe.getMessage());
        } finally{
            try { os.close(); } catch (Exception ignore) {}
        }
        
        return httpResponse;
    }
    
    private void setHeaders(HttpURLConnection connection)
    {
        if (getRequestHeaders() != null) {
            for (String key : getRequestHeaders().keySet()){
                connection.setRequestProperty(key, getRequestHeaders().get(key));
                //logger.debug("Setting Request Header: {}: {}", key, getRequestHeaders().get(key));
            }
        }
    }
}

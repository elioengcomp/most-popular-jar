package marcolino.elio.mpj;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import marcolino.elio.mpj.integration.RestClient;
import marcolino.elio.mpj.integration.RestClientException;
import marcolino.elio.mpj.utils.Constants;


public class RestClientTest {

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidClient() {
        new RestClient(null);        
    }
    
    @Test
    public void testRequestWithoutMethodArgument() {
        RestClient client = new RestClient(Constants.INVALID_PATH);
        
        try {
            client.request(null, "path", null, null, null);
            fail("Exception not caught");
        } catch (RestClientException e) {
            assertEquals("Method and path are required", e.getMessage());
        }
    }
    
    @Test
    public void testRequestWithoutPathArgument() {
        RestClient client = new RestClient(Constants.INVALID_PATH);
        try {
            client.request(RestClient.Method.GET, null, null, null, null);
            fail("Exception not caught");
        } catch (RestClientException e) {
            assertEquals("Method and path are required", e.getMessage());
        }
    }
    
    @Test
    public void testRequestWithoutContentTypeArgument() {
        RestClient client = new RestClient(Constants.INVALID_PATH);
        try {
            client.request(RestClient.Method.POST, "path", "payload", null, null);
            fail("Exception not caught");
        } catch (RestClientException e) {
            assertEquals("Payload requires a content type", e.getMessage());
        }
    }
    
    @Test
    public void testRequestPathNotFound() {
        RestClient client = new RestClient(Constants.INVALID_PATH);
        try {
            client.request(RestClient.Method.GET, "/invalid_path", null, null, null);
            fail("Exception not caught");
        } catch (RestClientException e) {            
            assertEquals("Failed to perform request", e.getMessage());
        }
    }
    
    @Test
    public void testSuccessfulGetRequest() {
        RestClient client = new RestClient(Constants.JSONPLACEHOLDER_URL);
        try {
            String response = client.request(RestClient.Method.GET, "/posts/1", null, null, null);
            JSONObject json = new JSONObject(response);
            assertEquals(1, json.get("userId"));
            assertEquals(1, json.get("id"));
            assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", json.get("title"));
        } catch (RestClientException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testSuccessfulPostRequest() {
        RestClient client = new RestClient(Constants.JSONPLACEHOLDER_URL);
        
        JSONObject payload = new JSONObject();
        payload.put("userId", 1);
        payload.put("title", "test");
        payload.put("body", "test body");
        
        try {
            String response = client.request(RestClient.Method.POST, "/posts", payload.toString(), "application/json", null);
            JSONObject json = new JSONObject(response);
            assertEquals(1, json.get("userId"));
            assertEquals(101, json.get("id"));
            assertEquals("test", json.get("title"));
        } catch (RestClientException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    
}

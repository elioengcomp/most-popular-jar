package marcolino.elio.mpj.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Generic client for REST APIs
 * 
 * @author elio
 *
 */
public class RestClient {

    private String basePath;

    /**
     * Create new client for the service running on the basePath informed
     * 
     * @param basePath
     *            path to the remote service
     */
    public RestClient(String basePath) {
        super();
        
        if(StringUtils.isBlank(basePath)) {
            throw new IllegalArgumentException("basePath must be a non blank string");
        }
        
        this.basePath = basePath;
    }

    public static enum Method {
        GET, HEAD, POST, PUT, DELETE,
    }

    public int getRequestTimeout() {

        return 60000;
    }

    /**
     * Execute request
     * 
     * @param method
     *            HTTP method
     * @param path
     *            path to the resource
     * @param payload
     *            string to be sent to the server
     * @param contentType
     *            HHTP type of the payload
     * @param headers
     *            Map with additional headers
     * @return String with response received form server
     * @throws RestClientException
     */
    public String request(Method method, String path, String payload, String contentType, Map<String, String> headers) throws RestClientException {

        // Validate arguments
        if (method == null || StringUtils.isBlank(path)) {
            throw new RestClientException("Method and path are required");
        }

        if (StringUtils.isNotBlank(payload) && StringUtils.isBlank(contentType)) {
            throw new RestClientException("Payload requires a content type");
        }

        HttpURLConnection conn = null;

        try {

            // Open Connection
            URL url = new URL(basePath + path);
            conn = (HttpURLConnection) url.openConnection();

            // Set timeouts
            int timeout = getRequestTimeout();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            
            // Add custom headers if present
            if (headers != null && !headers.isEmpty()) {
                for(Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // Set HTTP Method
            conn.setRequestMethod(method.name());

            // Send payload if present
            if (!StringUtils.isBlank(payload)) {
                conn.setRequestProperty("Content-Type", contentType);
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.flush();
            }

            // Verify HTTP response code
            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode > 299) {
                throw new RestClientException(responseCode, "Request failed with status: " + responseCode);
            }

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            String response = responseBuilder.toString();

            return response;

        } catch (ProtocolException e) {
            throw new RestClientException("Invalid Protocol", e);
        } catch (MalformedURLException e) {
            throw new RestClientException("Invalid URL", e);
        } catch (IOException e) {
            throw new RestClientException("Failed to perform request", e);
        } finally {

            // Close Connection
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Execute request
     * 
     * @param method
     *            HTTP method
     * @param path
     *            path to the resource     
     * @param headers
     *            Map with additional headers
     * @return String with response received form server
     * @throws RestClientException
     */
    public String request(Method method, String path, Map<String, String> headers) throws RestClientException {
        return this.request(method, path, null, null, headers);
    }

}

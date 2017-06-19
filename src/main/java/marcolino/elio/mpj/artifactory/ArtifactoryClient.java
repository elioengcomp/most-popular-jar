package marcolino.elio.mpj.artifactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.artifactory.model.ArtifactStats;
import marcolino.elio.mpj.rest.RestClient;
import marcolino.elio.mpj.rest.RestClientException;

/**
 * This class implements methods of integration with Artifactory
 * 
 * @author elio
 *
 */
public class ArtifactoryClient {

    private String artifactoryUrl;

    private String userToken;

    private Map<String, String> authenticationHeader;

    private RestClient restClient;

    /**
     * Create client to the artifactory service
     * 
     * @param artifactoryUrl
     *            Path to the artifactory service. Format: http|s://host:port
     * @param userToken
     *            Authentication token to be used when sending requests to the
     *            server
     */
    public ArtifactoryClient(String artifactoryUrl, String userToken) {
        super();

        if (artifactoryUrl == null) {
            throw new IllegalArgumentException("artifactoryUrl must be a non blank string");
        }

        this.artifactoryUrl = artifactoryUrl;
        this.userToken = userToken;

        this.createCredentials();
        this.createRestClient();
    }

    private void createCredentials() {

        if (StringUtils.isNotBlank(userToken)) {
            authenticationHeader = new HashMap<>();
            authenticationHeader.put("X-JFrog-Art-Api", this.userToken);
        }
    }

    private void createRestClient() {

        this.restClient = new RestClient(this.artifactoryUrl);
    }

    public void setRestClient(RestClient restClient) {

        this.restClient = restClient;
    }
    
    /**
     * Execute query
     * 
     * @param aql
     *            Query to be executed
     * @return
     * @throws ArtifactoryClientException
     */
    public List<Artifact> queryItems(String aql) throws ArtifactoryClientException {

        //Validate arguments
        if (StringUtils.isBlank(aql)) {
            throw new ArtifactoryClientException("aql must be a non blank string");
        }
        
        try {
            // Make request
            String response = restClient.request(RestClient.Method.POST, "/api/search/aql", aql, "text/plain", authenticationHeader);

            // Convert response to array of objects
            List<Artifact> result = new ArrayList<>();
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                result.add(new Artifact(results.getJSONObject(i)));
            }

            return result;

        } catch (RestClientException e) {
            throw new ArtifactoryClientException("Failed to execute query", e);
        }
    }

    /**
     * Execute paginated query
     * 
     * @param aql
     *            Query to be executed
     * @return
     * @throws ArtifactoryClientException
     */
    public List<Artifact> queryItemsPaginated(String aql) throws ArtifactoryClientException {

        //Validate arguments
        if (StringUtils.isBlank(aql)) {
            throw new ArtifactoryClientException("aql must be a non blank string");
        }
        
        List<Artifact> result = new ArrayList<>();

        // Get page size from artifactory configuration
        int limit = getQueryResultsLimit();

        // Set current page result to page size so the query is performed at
        // least one time
        int currentPageResults = limit;
        int currentPage = 0;

        // Query pages until the number of results is lesser than page size
        while (currentPageResults == limit) {
            List<Artifact> pageResults = this.queryItemsPage(aql, currentPage * limit, limit);
            result.addAll(pageResults);

            currentPageResults = pageResults.size();
            currentPage++;
        }

        return result;
    }

    /**
     * Query artifacts page
     * 
     * @param aql
     *            Query to be executed
     * @param offset
     *            Page offset
     * @param limit
     *            Page size
     * @return list with the page results
     * @throws ArtifactoryClientException
     */
    public List<Artifact> queryItemsPage(String aql, int offset, int limit) throws ArtifactoryClientException {

        // Add pagination properties
        StringBuilder paginatedAql = new StringBuilder();
        paginatedAql.append(aql);
        paginatedAql.append(".offset(").append(offset).append(")");
        paginatedAql.append(".limit(").append(limit).append(")");

        try {
            // Make request
            String response = restClient.request(RestClient.Method.POST, "/api/search/aql", paginatedAql.toString(), "text/plain", authenticationHeader);

            // Convert response to array of objects
            List<Artifact> result = new ArrayList<>();
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                result.add(new Artifact(results.getJSONObject(i)));
            }

            return result;

        } catch (RestClientException e) {
            throw new ArtifactoryClientException("Failed to execute query", e);
        }
    }

    /**
     * Get the query results limit size from artifactory configuration
     * 
     * @return
     * @throws ArtifactoryClientException
     */
    public int getQueryResultsLimit() throws ArtifactoryClientException {

        try {
            String response = restClient.request(RestClient.Method.GET, "/api/system", authenticationHeader);

            Pattern pattern = Pattern.compile("artifactory.search.userQueryLimit(\\s+)\\|\\s(\\d+)");
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(2));
            } else {
                throw new ArtifactoryClientException("Property artifactory.search.userQueryLimit not found");
            }
        } catch (RestClientException e) {
            throw new ArtifactoryClientException("Failed to get query results limit: " + e.getMessage(), e);
        }
    }

    /**
     * Get artifact stats
     * 
     * @param artifact
     *            artifact data
     * @return artifact stats
     * @throws ArtifactoryClientException
     */
    public ArtifactStats getArtifactStats(Artifact artifact) throws ArtifactoryClientException {

        // Validate arguments
        if (artifact == null) {
            throw new ArtifactoryClientException("artifact can't be null");
        }

        // Configure path
        StringBuilder path = new StringBuilder();
        path.append("/api/storage/").append(artifact.getRepositoryPath()).append("?stats");

        try {
            // Make request
            String response = restClient.request(RestClient.Method.GET, path.toString(), authenticationHeader);

            // Convert response to model
            return new ArtifactStats(new JSONObject(response));

        } catch (RestClientException e) {
            throw new ArtifactoryClientException("Failed to get artifact stats", e);
        }

    }

}

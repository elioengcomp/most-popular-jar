package marcolino.elio.mpj.artifactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.artifactory.model.ArtifactStats;
import marcolino.elio.mpj.rest.RestClient;
import marcolino.elio.mpj.rest.RestClientException;
import marcolino.elio.mpj.test.utils.Constants;


public class ArtifactoryClientTest {

    private RestClient mockedRestClient;
    private ArtifactoryClient client;
    
    @Before
    public void initMock() throws RestClientException {
        mockedRestClient = mock(RestClient.class);
        
        String searchResponse = "{\"results\" : [ {  \"repo\" : \"libs-release-local\",  \"path\" : \"marcolino/elio/artifact/1.0.0\",  \"name\" : \"artifact-1.0.0.jar\",  \"type\" : \"file\",  \"size\" : 33189,  \"created\" : \"2015-05-26T18:15:54.880Z\",  \"created_by\" : \"eduardo\",  \"modified\" : \"2015-05-26T18:15:54.875Z\",  \"modified_by\" : \"eduardo\",  \"updated\" : \"2015-05-26T18:15:54.875Z\"}],\"range\" : {  \"start_pos\" : 0,  \"end_pos\" : 1,  \"total\" : 1}}";
        when(mockedRestClient.request(eq(RestClient.Method.POST), eq("/api/search/aql"), anyString(), anyString(), anyMap())).thenReturn(searchResponse);
        
        String systemResponse = " SYSTEM INFORMATION DUMP  ======================= User Info ========================   user.country                                                          | US   user.dir                                                              | /var/opt/jfrog/artifactory   user.home                                                             | /var/opt/jfrog/artifactory   artifactory.search.userQueryLimit                                     | 1000 user.language                                                         | en   ";
        when(mockedRestClient.request(eq(RestClient.Method.GET), eq("/api/system"), anyMap())).thenReturn(systemResponse);
        
        String statsResponse = "{\"uri\" : \"https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/1.0.0/artifact-1.0.0.jar\",  \"downloadCount\" : 4,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/"), anyMap())).thenReturn(statsResponse);

        client = new ArtifactoryClient(Constants.ARTIFACTORY_PATH, Constants.USER_TOKEN);
        client.setRestClient(mockedRestClient);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidClient() {
        new ArtifactoryClient(null, Constants.USER_TOKEN);        
    }
    
    @Test
    public void testQueryItemsWithoutQuery() {
        try {
            client.queryItems("");
            fail("Exception not caught");
        } catch (ArtifactoryClientException e) {
            assertEquals("aql must be a non blank string", e.getMessage());
        }
    }
    
    @Test
    public void testQueryItems() throws RestClientException {
        try {
            StringBuilder aql = new StringBuilder();
            aql.append("items.find({");
            aql.append("\"repo\":{\"$eq\":\"").append(Constants.REPOSITORY).append("\"},");
            aql.append("\"name\":{\"$match\":\"").append("*.jar").append("\"}");
            aql.append("})");
            
            List<Artifact> artifacts = client.queryItems(aql.toString());
            assertEquals(1, artifacts.size());
        } catch (ArtifactoryClientException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testGetQueryResultsLimit() {
        try {
            int limit = client.getQueryResultsLimit();  
            assertEquals(1000, limit);
        } catch (ArtifactoryClientException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test(expected=ArtifactoryClientException.class)
    public void testGetQueryResultsLimitPropertyNotFound() throws RestClientException, ArtifactoryClientException {
        
        when(mockedRestClient.request(eq(RestClient.Method.GET), eq("/api/system"), anyMap())).thenReturn("Something else not expected");
        client.getQueryResultsLimit();
    }
    
    @Test
    public void testGetArtifactStatsWithoutArtifact() {
        try {
            client.getArtifactStats(null);
            fail("Exception not caught");
        } catch (ArtifactoryClientException e) {
            assertEquals("artifact can't be null", e.getMessage());
        }
    }
    
    @Test
    public void testGetArtifactStats() {        
        try {
            Artifact artifact = new Artifact("artifact-1.0.0.jar", "marcolino/elio/artifact/1.0.0", "libs-release-local");
            ArtifactStats stats = client.getArtifactStats(artifact);
            assertEquals(4, stats.getDownloadCount());
        } catch (ArtifactoryClientException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    
}

package marcolino.elio.mpj.worker;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.rest.RestClient;
import marcolino.elio.mpj.rest.RestClientException;
import marcolino.elio.mpj.test.utils.Constants;
import marcolino.elio.mpj.worker.GetArtifactDownloadCountWorker;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;


public class GetArtifactDownloadCountWorkerTest {

    private RestClient mockedRestClient;
    private ArtifactoryClient client;
    
    @Before
    public void initClient() throws RestClientException {
        mockedRestClient = mock(RestClient.class);
        
        String statsResponse = "{\"uri\" : \"https://artifactory.marcolino.com/artifactory/repo-local/com/marcolino/artifact/1.0.0/artifact-1.0.0.jar\",  \"downloadCount\" : 4,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/"), anyMap())).thenReturn(statsResponse);
        
        client = new ArtifactoryClient(Constants.ARTIFACTORY_PATH, Constants.USER_TOKEN);
        client.setRestClient(mockedRestClient);
    }
    
    @Test
    public void testWorker() throws Exception {
        
        Artifact artifact = new Artifact("artifact-1.0.0.jar", "com/marcolino/artifact/1.0.0", "repo-local");
        GetArtifactDownloadCountWorker worker = new GetArtifactDownloadCountWorker(client, artifact);
        ArtifactDownloadCount result = worker.call();
        assertEquals(artifact, result.getArtifact());
        assertEquals(Integer.valueOf(4), result.getDownloadCount());
        
    }
    
    @Test(expected=WorkerException.class)
    public void testError() throws Exception {
        
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/"), anyMap())).thenThrow(new RestClientException(404, "Not found!"));
        
        Artifact artifact = new Artifact("artifact-1.0.0.jar", "com/marcolino/artifact/1.0.0", "repo-local");
        GetArtifactDownloadCountWorker worker = new GetArtifactDownloadCountWorker(client, artifact);
        worker.call();     
    }
    
    
}

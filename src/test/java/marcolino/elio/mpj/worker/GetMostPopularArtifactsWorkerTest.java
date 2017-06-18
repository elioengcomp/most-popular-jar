package marcolino.elio.mpj.worker;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.rest.RestClient;
import marcolino.elio.mpj.rest.RestClientException;
import marcolino.elio.mpj.test.utils.Constants;
import marcolino.elio.mpj.worker.GetArtifactDownloadCountWorker;
import marcolino.elio.mpj.worker.GetMostPopularArtifactsWorker;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;


public class GetMostPopularArtifactsWorkerTest {

    private static RestClient mockedRestClient;
    private ArtifactoryClient client;
    
    @BeforeClass
    public static void initMock() throws RestClientException {
        mockedRestClient = mock(RestClient.class);
    }
    
    @Before
    public void initClient() {
        client = new ArtifactoryClient(Constants.ARTIFACTORY_PATH, Constants.USER_TOKEN);
        client.setRestClient(mockedRestClient);
    }
    
    private List<Artifact> createArtifactsList() {        
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(new Artifact("artifact-1.0.0.jar", "marcolino/elio/artifact/1.0.0", "libs-release-local"));
        artifacts.add(new Artifact("artifact-2.0.0.jar", "marcolino/elio/artifact/2.0.0", "libs-release-local"));
        artifacts.add(new Artifact("artifact-3.0.0.jar", "marcolino/elio/artifact/3.0.0", "libs-release-local"));
        return artifacts;
    }
    
    @Test
    public void testWorker() throws Exception {
        
        List<Artifact> artifacts = createArtifactsList();
        
        String statsResponse = "{\"uri\" : \"https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/1.0.0/artifact-1.0.0.jar\",  \"downloadCount\" : 1,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/libs-release-local/marcolino/elio/artifact/1.0.0"), anyMap())).thenReturn(statsResponse);
        
        statsResponse = "{\"uri\" : \"https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/2.0.0/artifact-2.0.0.jar\",  \"downloadCount\" : 2,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/libs-release-local/marcolino/elio/artifact/2.0.0"), anyMap())).thenReturn(statsResponse);
        
        statsResponse = "{\"uri\" : \"https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/3.0.0/artifact-3.0.0.jar\",  \"downloadCount\" : 3,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("/api/storage/libs-release-local/marcolino/elio/artifact/3.0.0"), anyMap())).thenReturn(statsResponse);
        
        GetMostPopularArtifactsWorker worker = new GetMostPopularArtifactsWorker(1, client, artifacts, 2, 1);
        List<ArtifactDownloadCount> result = worker.call();
        assertEquals(2, result.size());
        assertEquals(3, result.get(0).getDownloadCount().intValue());
        assertEquals(2, result.get(1).getDownloadCount().intValue());
    }

    
    
}
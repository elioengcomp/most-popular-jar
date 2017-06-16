package marcolino.elio.mpj;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.integration.RestClient;
import marcolino.elio.mpj.integration.RestClientException;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.model.Artifact;
import marcolino.elio.mpj.utils.Constants;
import marcolino.elio.mpj.worker.GetArtifactDownloadCountWorker;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;


public class GetArtifactDownloadCountWorkerTest {

    private static RestClient mockedRestClient;
    private ArtifactoryClient client;
    
    @BeforeClass
    public static void initMock() throws RestClientException {
        mockedRestClient = mock(RestClient.class);
        
        String statsResponse = "{\"uri\" : \"https://services.contabilizei.com/artifactory/libs-release-local/br/com/contabilizei/contabilizei-sso-client/1.3.2/contabilizei-sso-client-1.3.2.jar\",  \"downloadCount\" : 4,  \"lastDownloaded\" : 1497276222083,  \"lastDownloadedBy\" : \"fernando\",  \"remoteDownloadCount\" : 0,  \"remoteLastDownloaded\" : 0}";
        when(mockedRestClient.request(eq(RestClient.Method.GET), startsWith("storage/"), anyMap())).thenReturn(statsResponse);
    }
    
    @Before
    public void initClient() {
        client = new ArtifactoryClient(Constants.ARTIFACTORY_PATH, Constants.USER_TOKEN);
        client.setRestClient(mockedRestClient);
    }
    
    @Test
    public void testWorker() throws Exception {
        
        Artifact artifact = new Artifact("contabilizei-sso-client-1.3.2.jar", "br/com/contabilizei/contabilizei-sso-client/1.3.2", "libs-release-local");
        GetArtifactDownloadCountWorker worker = new GetArtifactDownloadCountWorker(client, artifact);
        ArtifactDownloadCount result = worker.call();
        assertEquals(artifact, result.getArtifact());
        assertEquals(Integer.valueOf(4), result.getDownloadCount());
        
    }
    
    
}

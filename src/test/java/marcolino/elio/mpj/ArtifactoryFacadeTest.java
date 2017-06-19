package marcolino.elio.mpj;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marcolino.elio.mpj.CommandLineHandler;
import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.artifactory.model.ArtifactStats;
import marcolino.elio.mpj.rest.RestClient;
import marcolino.elio.mpj.rest.RestClientException;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

public class ArtifactoryFacadeTest {

    private ArtifactoryClient mockedArtifactoryClient;
    
    @Before
    public void initMock() throws ArtifactoryClientException {
        mockedArtifactoryClient = mock(ArtifactoryClient.class);
        
        List<Artifact> artifacts = createArtifactsList();
        List<ArtifactStats> artifactStats = createArtifactStatsList();
        
        when(mockedArtifactoryClient.getQueryResultsLimit()).thenReturn(1000);
        when(mockedArtifactoryClient.queryItemsPage(anyString(), anyInt(), anyInt())).thenReturn(artifacts);
        when(mockedArtifactoryClient.queryItems(anyString())).thenReturn(artifacts);
        when(mockedArtifactoryClient.getArtifactStats(artifacts.get(0))).thenReturn(artifactStats.get(0));
        when(mockedArtifactoryClient.getArtifactStats(artifacts.get(1))).thenReturn(artifactStats.get(1));
        when(mockedArtifactoryClient.getArtifactStats(artifacts.get(2))).thenReturn(artifactStats.get(2));
    }
    
    private List<Artifact> createArtifactsList() {        
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(new Artifact("artifact-1.0.0.jar", "marcolino/elio/artifact/1.0.0", "libs-release-local"));
        artifacts.add(new Artifact("artifact-2.0.0.jar", "marcolino/elio/artifact/2.0.0", "libs-release-local"));
        artifacts.add(new Artifact("artifact-3.0.0.jar", "marcolino/elio/artifact/3.0.0", "libs-release-local"));
        return artifacts;
    }
    
    private List<ArtifactStats> createArtifactStatsList() {        
        List<ArtifactStats> artifactStats = new ArrayList<>();
        artifactStats.add(new ArtifactStats("https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/1.0.0/artifact-1.0.0.jar", 1));
        artifactStats.add(new ArtifactStats("https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/2.0.0/artifact-2.0.0.jar", 2));
        artifactStats.add(new ArtifactStats("https://artifactory.marcolino.com/artifactory/libs-release-local/marcolino/elio/artifact/3.0.0/artifact-3.0.0.jar", 3));
        return artifactStats;
    }
    
    @Test
    public void testGetListItemsFromRepoByNameQuery() {
        
        ArtifactoryFacade facade = new ArtifactoryFacade(mockedArtifactoryClient);
        
        String expected = "items.find({\"repo\":{\"$eq\":\"repo\"},\"name\":{\"$match\":\"name\"}})";
        String result = facade.getListItemsFromRepoByNameQuery("repo", "name");
        assertEquals(expected, result);
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetMostPopularJarWithNoPageSize() throws ArtifactoryClientException {
        
        ArtifactoryFacade facade = new ArtifactoryFacade(mockedArtifactoryClient);
        
        facade.getMostPopularJar("libs-release-local", 2, 1, 1, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetMostPopularJarWithPageSizeZero() throws ArtifactoryClientException {
        
        ArtifactoryFacade facade = new ArtifactoryFacade(mockedArtifactoryClient);
        
        facade.getMostPopularJar("libs-release-local", 2, 1, 1, 0);
    }
    
    @Test
    public void testGetMostPopularJarWithPageSize() throws ArtifactoryClientException {
        
        ArtifactoryFacade facade = new ArtifactoryFacade(mockedArtifactoryClient);
        
        List<ArtifactDownloadCount> result = facade.getMostPopularJar("libs-release-local", 2, 1, 1, 10);
        
        assertEquals("Ranking size", 2, result.size());
        assertEquals("First place download count", 3, result.get(0).getDownloadCount().intValue());
        assertEquals("Second place download count", 2, result.get(1).getDownloadCount().intValue());
        assertEquals("First place artifact", "artifact-3.0.0.jar", result.get(0).getArtifact().getName());
        assertEquals("Second place artifact", "artifact-2.0.0.jar", result.get(1).getArtifact().getName());
    }
    
}

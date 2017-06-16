package marcolino.elio.mpj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.integration.artifactory.model.Artifact;


public class ArtifactTest {

    @Test
    public void testGetArtifactPath() {
        Artifact artifact = new Artifact("test-api-1.0.0.jar", "marcolino/elio/test-api/1.0.0", "repo-local");
        
        assertEquals("repo-local/marcolino/elio/test-api/1.0.0/test-api-1.0.0.jar", artifact.getRepositoryPath());
    }
    
}

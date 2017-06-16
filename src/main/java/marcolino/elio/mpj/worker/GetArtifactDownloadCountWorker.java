package marcolino.elio.mpj.worker;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.integration.artifactory.model.Artifact;
import marcolino.elio.mpj.integration.artifactory.model.ArtifactStats;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Task responsible for get the download count of an artifact 
 * @author elio
 *
 */
public class GetArtifactDownloadCountWorker implements Callable<ArtifactDownloadCount>{
    
    private static final Logger logger = Logger.getLogger(GetArtifactDownloadCountWorker.class.getName());

    private ArtifactoryClient artifactoryClient;
    private Artifact artifact;
    
    public GetArtifactDownloadCountWorker(ArtifactoryClient artifactoryClient, Artifact artifact) {
        super();
        this.artifactoryClient = artifactoryClient;
        this.artifact = artifact;
    }
    
    @Override
    public ArtifactDownloadCount call() {
        
        try {
            ArtifactStats stats = artifactoryClient.getArtifactStats(artifact);
            return new ArtifactDownloadCount(artifact, stats.getDownloadCount());
            
        } catch (ArtifactoryClientException e) {
            throw new WorkerException("Failed to get Artifact download count", e);
        }
    }

}

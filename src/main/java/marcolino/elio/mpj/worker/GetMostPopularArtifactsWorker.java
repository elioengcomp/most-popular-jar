package marcolino.elio.mpj.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.artifactory.utils.ArtifactDownloadCountRanking;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Task responsible for finding the nth most popular in a collection of artifacts 
 * @author elio
 *
 */
public class GetMostPopularArtifactsWorker implements Callable<List<ArtifactDownloadCount>>{

    private static final Logger logger = Logger.getLogger(GetMostPopularArtifactsWorker.class.getName());
    
    private int workerId;
    private ArtifactoryClient artifactoryClient;
    private List<Artifact> artifacts;
    private int rankingSize;
    private int numberOfThreads;
    
    /**
     * Woker Constructor
     * @param workerId
     *              Worker identification
     * @param artifactoryClient
     *              Artifactory client
     * @param artifacts
     *              Collection of artifacts to analyze 
     * @param rankingSize
     *              Number of positions to return
     * @param numberOfThreads
     *              Number of threads this worker can use to process the payload
     */
    public GetMostPopularArtifactsWorker(int workerId, ArtifactoryClient artifactoryClient, List<Artifact> artifacts, int rankingSize, int numberOfThreads) {
        super();
        this.workerId = workerId;
        this.artifactoryClient = artifactoryClient;
        this.artifacts = artifacts;
        this.rankingSize = rankingSize;
        this.numberOfThreads = numberOfThreads;
    }
    
    @Override
    public List<ArtifactDownloadCount> call() {
     
        logger.info(String.format("%s - %s %s %s", workerId, "Starting worker for", artifacts.size(), "artifacts"));
        
        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(rankingSize);
        List<Future<ArtifactDownloadCount>> workersFuture = new ArrayList<>();
        
        // Create Thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        // Get artifacts download count
        for (Artifact artifact : artifacts) {
            GetArtifactDownloadCountWorker worker = new GetArtifactDownloadCountWorker(artifactoryClient, artifact);
            workersFuture.add(executor.submit(worker));
        }
        
        try {
            // Update ranking
            for (int i = 0; i < workersFuture.size(); i++) {
                
                logProgress(i + 1, workersFuture.size());
                
                if (!Thread.interrupted()) {
                    ArtifactDownloadCount artifactDownloadCount;
                    artifactDownloadCount = workersFuture.get(i).get();
                    ranking.updateRanking(artifactDownloadCount);
                }
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WorkerException("Execution interrupted");
        } catch (ExecutionException e) {
            throw new WorkerException("Failed to get most popular artifacts", e);
        } finally {
            executor.shutdownNow();
        }
        
        logger.info(String.format("%s - %s", workerId, "Worker finished!"));
        
        return ranking.getRanking();
    }
    
    public void logProgress(int index, int workSize) {
        if(index % 100 == 0) {
            logger.info(String.format("%s - %s/%s %s", workerId, index, workSize, "artifacts processed"));
        }        
    }
    

}

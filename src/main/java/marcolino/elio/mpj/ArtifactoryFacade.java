package marcolino.elio.mpj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.artifactory.model.Artifact;
import marcolino.elio.mpj.artifactory.utils.ArtifactDownloadCountRanking;
import marcolino.elio.mpj.rest.RestClientException;
import marcolino.elio.mpj.worker.GetMostPopularArtifactsWorker;
import marcolino.elio.mpj.worker.WorkerException;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Business logic related with artifactory integration 
 * @author elio
 *
 */
public class ArtifactoryFacade {
    
    private static final Logger logger = Logger.getLogger(ArtifactoryFacade.class.getName());
    
    private ArtifactoryClient client;
    
    public ArtifactoryFacade(ArtifactoryClient client) {
        super();
        this.client = client;
    }

    /**
     * Get ranking of most popular jar files in repository
     * @param repository
     *                  respository to look up for files
     * @param rankingSize
     *                  size of the ranking returned
     * @param maxConcurrentWorkers
     *                  max number of concurrent workers
     * @param threadsPerWorker
     *                  max number of threads per worker
     * @param itemsPerWorker
     *                  number of items to be processed per each worker 
     * @return list of the most downloaded jar files inside the repository
     * @throws ArtifactoryClientException
     */
    public List<ArtifactDownloadCount> getMostPopularJar(String repository, int rankingSize, int maxConcurrentWorkers, int threadsPerWorker, Integer itemsPerWorker) throws ArtifactoryClientException {
        
        //Validate arguments
        if (itemsPerWorker == null || itemsPerWorker < 1) {
            throw new IllegalArgumentException("ItemsPerWorker must be a positive integer");
        }
        
        List<Future<List<ArtifactDownloadCount>>> workersFuture = new ArrayList<>();
        ExecutorService workersExecutor = Executors.newFixedThreadPool(maxConcurrentWorkers);
        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(rankingSize);
        
        // Artifactory query
        String aql = getListItemsFromRepoByNameQuery(repository, "*.jar");
        
        try {
            
            // Query items
            List<Artifact> results = client.queryItems(aql);            
            
            // Distribute work load between workers accordingly to itemsPerWorker value  
            int listWorkerInitIndex = 0;
            int workersCounter = 0;
            while(listWorkerInitIndex < results.size()) {
                
                int listWorkerFinalIndex = listWorkerInitIndex + itemsPerWorker;
                if (listWorkerFinalIndex > results.size()) {
                    listWorkerFinalIndex = results.size();
                }
                
                List<Artifact> workerList = results.subList(listWorkerInitIndex, listWorkerFinalIndex);
                GetMostPopularArtifactsWorker worker = new GetMostPopularArtifactsWorker(workersCounter + 1, client, workerList, rankingSize, threadsPerWorker);                
                workersFuture.add(workersExecutor.submit(worker));
                
                listWorkerInitIndex += itemsPerWorker;
                workersCounter++;
            }
        
            // Keep ranking updated
            for (int i = 0; i < workersFuture.size(); i++) {
                List<ArtifactDownloadCount> workerRanking = workersFuture.get(i).get();
                ranking.updateRanking(workerRanking);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArtifactoryClientException("Execution interrupted");
        } catch (ExecutionException e) {
            throw new ArtifactoryClientException("Failed to get most popular jars", e);
        } finally {
            workersExecutor.shutdownNow();
        }
        
        return ranking.getRanking();
    }
    
    /**
     * Configure a query that return items that match the name pattern inside a
     * repository
     * 
     * @param repository
     *            Name of the repository to loog for items
     * @param name
     *            Pattern of the name to look for
     * @return a String containing the query
     */
    public String getListItemsFromRepoByNameQuery(String repository, String name) {

        // Configure query
        StringBuilder aql = new StringBuilder();
        aql.append("items.find({");
        aql.append("\"repo\":{\"$eq\":\"").append(repository).append("\"},");
        aql.append("\"name\":{\"$match\":\"").append(name).append("\"}");
        aql.append("})");
        return aql.toString();
    }

}

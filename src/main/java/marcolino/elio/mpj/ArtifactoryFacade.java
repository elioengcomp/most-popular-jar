package marcolino.elio.mpj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.integration.artifactory.model.Artifact;
import marcolino.elio.mpj.utils.ArtifactDownloadCountRanking;
import marcolino.elio.mpj.worker.GetMostPopularArtifactsWorker;
import marcolino.elio.mpj.worker.WorkerException;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Business logic related with artifactory integration 
 * @author elio
 *
 */
public class ArtifactoryFacade {
    
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
     *                  number of items to be processed per each worker. Default: artifactory.search.userQueryLimit 
     * @return list of the most downloaded jar files inside the repository
     * @throws ArtifactoryClientException
     */
    public List<ArtifactDownloadCount> getMostPopularJar(String repository, int rankingSize, int maxConcurrentWorkers, int threadsPerWorker, Integer itemsPerWorker) throws ArtifactoryClientException {
        
        List<Future<List<ArtifactDownloadCount>>> workersFuture = new ArrayList<>();
        ExecutorService workersExecutor = Executors.newFixedThreadPool(maxConcurrentWorkers);
        ArtifactDownloadCountRanking ranking = new ArtifactDownloadCountRanking(rankingSize);
        
        // Artifactory query
        String aql = getListItemsFromRepoByNameQuery(repository, "*.jar");
        
        try {
            
            // Set number of items per page
            int pageSize = 0;
            if (itemsPerWorker != null && itemsPerWorker > 0) {
                pageSize = itemsPerWorker;
            } else {
                pageSize = client.getQueryResultsLimit();
            }
            
            // Set current page result to page size so the query is performed at least one time
            int currentPageResults = pageSize;
            int currentPage = 0;
    
            // Query pages until the number of results is lesser than page size
            while (currentPageResults == pageSize) {
                List<Artifact> pageResults = client.queryItemsPage(aql, currentPage * pageSize, pageSize);            
                currentPageResults = pageResults.size();
                currentPage++;
                
                //Submit results to processing
                if (!pageResults.isEmpty()) {
                    GetMostPopularArtifactsWorker worker = new GetMostPopularArtifactsWorker(currentPage, client, pageResults, rankingSize, threadsPerWorker);                
                    workersFuture.add(workersExecutor.submit(worker));
                }
            }
            
        
            // Keep ranking updated
            for (int i = 0; i < workersFuture.size(); i++) {
                List<ArtifactDownloadCount> workerRanking = workersFuture.get(i).get();
                ranking.updateRanking(workerRanking);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WorkerException("Execution interrupted");
        } catch (ExecutionException e) {
            throw new WorkerException("Failed to get most popular jars", e);
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

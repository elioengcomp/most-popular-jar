package marcolino.elio.mpj.artifactory.utils;

import java.util.ArrayList;
import java.util.List;

import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Utility methods about ranking ArtifactDownloadCount
 * @author elio
 *
 */
public class ArtifactDownloadCountRanking {

    private int rankingSize;
    private List<ArtifactDownloadCount> ranking;
    
    public ArtifactDownloadCountRanking(int rankingSize){
        this.rankingSize = rankingSize;
        this.ranking = new ArrayList<>(rankingSize);
    }
        
    /**
     * Update artifacts ranking
     * @param ranking
     * @param newResult
     */
    public void updateRanking(ArtifactDownloadCount newResult) {
        
        // If ranking is empty add new result
        if(ranking.isEmpty()) {
            ranking.add(newResult);
        }
        
        // If ranking is full and new result is lesser than last element in ranking, ignore new result
        else if(ranking.size() == rankingSize && ranking.get(rankingSize-1).getDownloadCount() > newResult.getDownloadCount()) {
            return;
        }
        
        // Otherwise find element place in ranking
        else {
            
            // Try to switch elements and shift ranking
            boolean inserted = false;
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getDownloadCount() <= newResult.getDownloadCount()) {
                    ranking.add(i, newResult);                    
                    inserted = true;
                    break;
                }
            }
            
            /*  If did not found a place in the ranking it is the new lesser element,
             *  So add the element to the end of the ranking */
            if (!inserted) {
                ranking.add(newResult);
            }
        }
        
        // Keep just n elements in ranking
        while (ranking.size() > rankingSize) {
            ranking.remove(rankingSize);
        }
    }
    
    /**
     * Update artifacts ranking
     * @param ranking
     * @param listNewResult
     * @param rankingSize
     */
    public void updateRanking(List<ArtifactDownloadCount> listNewResult) {
        for (ArtifactDownloadCount newResult : listNewResult) {
            updateRanking(newResult);
        }
    }

    public List<ArtifactDownloadCount> getRanking() {
    
        return ranking;
    }  
    
}

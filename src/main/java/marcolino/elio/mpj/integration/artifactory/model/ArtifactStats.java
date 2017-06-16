package marcolino.elio.mpj.integration.artifactory.model;

import org.json.JSONObject;

/**
 * Stats from an Artifact present in the artifactory repository
 * @author elio
 *
 */
public class ArtifactStats {

    private String uri;
    private int downloadCount;
    
    public ArtifactStats() {
        super();
    }
    
    public ArtifactStats(JSONObject json) {
        this.uri = json.getString("uri");
        this.downloadCount = json.getInt("downloadCount");
    }
    
    public String getUri() {
    
        return uri;
    }
    
    public void setUri(String uri) {
    
        this.uri = uri;
    }
    
    public int getDownloadCount() {
    
        return downloadCount;
    }
    
    public void setDownloadCount(int downloadCount) {
    
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {

        return "ArtifactStats [uri=" + uri + ", downloadCount=" + downloadCount + "]";
    }
    
}

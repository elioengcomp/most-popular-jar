package marcolino.elio.mpj.artifactory.model;

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
    
    public ArtifactStats(String uri, int downloadCount) {
        super();
        this.uri = uri;
        this.downloadCount = downloadCount;
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

}

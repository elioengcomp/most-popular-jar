package marcolino.elio.mpj.worker.dto;

import marcolino.elio.mpj.integration.artifactory.model.Artifact;

public class ArtifactDownloadCount {

    private Artifact artifact;
    private Integer downloadCount;
    
    public ArtifactDownloadCount(Artifact artifact, Integer downloadCount) {
        super();
        this.artifact = artifact;
        this.downloadCount = downloadCount;
    }

    public Artifact getArtifact() {
    
        return artifact;
    }
    
    public void setArtifact(Artifact artifact) {
    
        this.artifact = artifact;
    }
    
    public Integer getDownloadCount() {
    
        return downloadCount;
    }
    
    public void setDownloadCount(Integer downloadCount) {
    
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {

        return "GetArtifactDownloadCountResult [artifact=" + artifact + ", downloadCount=" + downloadCount + "]";
    }
    
}

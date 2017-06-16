package marcolino.elio.mpj.integration.artifactory.model;

import org.json.JSONObject;

/**
 * Artifact present in the artifactory repository
 * @author elio
 *
 */
public class Artifact {

    private String name;
    private String path;
    private String repo;
    
    public Artifact() {
        super();
    }
    
    public Artifact(String name, String path, String repo) {
        super();
        this.name = name;
        this.path = path;
        this.repo = repo;
    }

    public Artifact(JSONObject json) {
        this.name = json.getString("name");
        this.path = json.getString("path");
        this.repo = json.getString("repo");
    }
    
    public String getName() {
    
        return name;
    }
    
    public void setName(String name) {
    
        this.name = name;
    }
    
    public String getPath() {
    
        return path;
    }
    
    public void setPath(String path) {
    
        this.path = path;
    }
    
    public String getRepo() {
    
        return repo;
    }
    
    public void setRepo(String repo) {
    
        this.repo = repo;
    }
    
    public String getRepositoryPath() {
        StringBuilder statsPath = new StringBuilder();
        statsPath.append(repo).append("/").append(path).append("/").append(name);
        return statsPath.toString();
    }

    @Override
    public String toString() {

        return "Artifact [name=" + name + ", path=" + path + ", repo=" + repo + "]";
    }
    
}

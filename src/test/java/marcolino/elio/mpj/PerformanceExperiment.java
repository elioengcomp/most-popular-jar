package marcolino.elio.mpj;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.artifactory.ArtifactoryClientException;

public class PerformanceExperiment {

    public static void main(String args[]) {
        
        // Configure Scenarios
        int[] workers = new int[]{2,3,5,8,13,21};
        int[] threads = new int[]{2,3,5,8,13,21};
        int[] pages = new int[]{10,20,50,100,200,500,1000};

        ArtifactoryClient client = new ArtifactoryClient("https://services.contabilizei.com/artifactory", "AKCp2WXgcVm7GSRfCbgjEpNet9hv5QiS6SSpr4di3MBBRL5BW9QdJeNNTdrYZogicNJ6rppm7");
        ArtifactoryFacade artifactory = new ArtifactoryFacade(client);
        
        StringBuilder results = new StringBuilder();
        
        long experimentInitialTime = System.currentTimeMillis();
        
        // Run experiments
        for (int worker : workers) {
            for (int thread : threads) {
                for (int page : pages) {
                    for (int i = 0; i < 5; i++) {
                        
                        System.out.println(String.format("Running experiment number %s for worker:%s thread:%s page:%s", i, worker, thread, page));
                        
                        try {
                            long initialTime = System.currentTimeMillis();
                            artifactory.getMostPopularJar("libs-release-local", 1, worker, thread, page);
                            long time = System.currentTimeMillis() - initialTime;
                            results.append(String.format("%s;%s;%s;%s", worker, thread, page, time)).append(System.lineSeparator());
                        } catch (ArtifactoryClientException e) {
                            e.printStackTrace();
                            results.append(String.format("%s;%s;%s;%s", worker, thread, page, "erro: " + e.getMessage())).append(System.lineSeparator());
                        }
                    }
                }
            }
        }
        
        System.out.println(String.format("Experiment time: %s minutes", (System.currentTimeMillis() - experimentInitialTime) / 1000 / 60));
        
        writeResults(results);
                
    }
    
    private static void writeResults(StringBuilder results) {
        // Console
        System.out.println(results.toString());
        
        // File
        try {
            StringBuilder dirPath = new StringBuilder();
            dirPath.append(System.getProperty("user.home")).append(File.separator).append("mpj-experiment");
            File dir = new File(dirPath.toString());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            StringBuilder outputPath = new StringBuilder();
            outputPath.append(dirPath).append(File.separator).append("results-").append(System.currentTimeMillis()).append(".txt");
            File outputFile = new File(outputPath.toString());
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            FileWriter writer = new FileWriter(outputFile);
            writer.write(String.format("%s;%s;%s;%s", "worker", "thread", "page", "time"));
            writer.write(System.lineSeparator());
            writer.write(results.toString()); 
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}

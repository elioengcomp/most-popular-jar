package marcolino.elio.mpj;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import marcolino.elio.mpj.integration.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.integration.artifactory.ArtifactoryClientException;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

public class Main {
    
    private static String url;
    private static String auth;
    private static String repo;
    private static int size;
    private static int workers;
    private static int threads;
    private static int page;
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(" ", options );
    }
    
    private static Options configureOptions() {
        Options options = new Options();
        options.addOption("u", "url", true, "Artifactory url in format http|s://host:port");
        options.addOption("r", "repo", true, "Repository to look for the most popular jar files");
        options.addOption("a", "auth", true, "Artifactory authentication token");
        options.addOption("s", "size", true, "Ranking size");
        options.addOption("w", "workers", true, "Max number of concurrent workers");
        options.addOption("t", "threads", true, "Max number of concurrent threads per worker");
        options.addOption("p", "page", true, "Page size per worker");
        options.addOption("h", "help", false, "Print this message");
        return options;
    }
    
    private static boolean parseArguments(String args[]) {
        
        CommandLineParser parser = new DefaultParser();
        Options options = configureOptions();
        
        try {
            CommandLine line = parser.parse(options, args);
                        
            if (line.hasOption("help") || 
                    !line.hasOption("url") ||
                    !line.hasOption("repo") ||
                    !line.hasOption("auth") ||
                    !line.hasOption("size") ||
                    !line.hasOption("workers") ||
                    !line.hasOption("threads")) {
                printHelp(options);
                return false;
            }
            
            url = line.getOptionValue("url");
            auth = line.getOptionValue("auth");
            repo = line.getOptionValue("repo");
            size = Integer.parseInt(line.getOptionValue("size"));
            workers = Integer.parseInt(line.getOptionValue("workers"));
            threads = Integer.parseInt(line.getOptionValue("threads"));
            
            if (line.hasOption("page")) {
                page = Integer.parseInt(line.getOptionValue("page"));
            }
            
        } catch (ParseException e1) {
            System.out.println("Invalid arguments: see -h option");
            System.exit(-1);
        }
        
        return true;
        
    }
    
    public static void main(String args[]) throws ArtifactoryClientException {
        
        if (parseArguments(args)) {
        
            long initialTime = System.currentTimeMillis();
        
            ArtifactoryClient artifactoryClient = new ArtifactoryClient(url, auth);    
            ArtifactoryFacade artifactoryFacade = new ArtifactoryFacade(artifactoryClient);            
            List<ArtifactDownloadCount> ranking = artifactoryFacade.getMostPopularJar(repo, size, workers, threads, page);
            
            System.out.println("Results:");
            for (ArtifactDownloadCount item : ranking) {
                System.out.println(item.getDownloadCount() + ": " + item.getArtifact().getRepositoryPath());
            }
            
            System.out.println("Time: " + (System.currentTimeMillis() - initialTime) + " ms");
            
            
        }
    }

}

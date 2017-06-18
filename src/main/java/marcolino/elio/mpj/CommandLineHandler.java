package marcolino.elio.mpj;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import marcolino.elio.mpj.artifactory.ArtifactoryClient;
import marcolino.elio.mpj.worker.dto.ArtifactDownloadCount;

/**
 * Handles executions of this app through command line
 * @author elio
 *
 */
public class CommandLineHandler {
    
    private static final Logger logger = Logger.getLogger(CommandLineHandler.class.getName());
    
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_ERROR = 1;
    private static final int EXIT_INVALID_ARGUMENTS_ERROR = 2;
    
    private String url;
    private String auth;
    private String repo;
    private int size = 2;
    private int workers = 1;
    private int threads = 1;
    private int page = 0;
    
    private HelpFormatter helpFormatter;
    
    public CommandLineHandler(HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }
    
    public void printHelp(Options options) {
        helpFormatter.printHelp(" ", options );
    }
    
    public Options configureOptions() {
        Options options = new Options();
        options.addOption("u", "url", true, "Artifactory url in format http|s://host:port/artifactory");
        options.addOption("r", "repo", true, "Repository to search for the most popular jar files");
        options.addOption("a", "auth", true, "Artifactory authentication token");
        options.addOption("s", "size", true, "(Optional) Ranking size. Default: 2");
        options.addOption("w", "workers", true, "(Optional) Max number of concurrent workers. Default: 1");
        options.addOption("t", "threads", true, "(Optional) Max number of concurrent threads per worker. Default: 1");
        options.addOption("p", "page", true, "Page size per worker. Optional if authentication token provided has admin privileges. In this case will use Artifactory instance artifactory.search.userQueryLimit property value");
        options.addOption("h", "help", false, "Print this message");
        return options;
    }
    
    public boolean parseArguments(String args[]) {
        
        CommandLineParser parser = new DefaultParser();
        Options options = configureOptions();
        
        try {
            CommandLine line = parser.parse(options, args);
                        
            if (line.hasOption("help")){
                printHelp(options);
                return false;
            }
            
            if(!line.hasOption("url") ||
               !line.hasOption("repo") ||
               !line.hasOption("auth") ){
                System.out.println("Invalid arguments: showing -h option");
                printHelp(options);
                throw new IllegalArgumentException();
                
            }
            
            url = line.getOptionValue("url");
            auth = line.getOptionValue("auth");
            repo = line.getOptionValue("repo");
            
            if (line.hasOption("size")) {
                size = Integer.parseInt(line.getOptionValue("size"));
            }
            
            if (line.hasOption("workers")) {
                workers = Integer.parseInt(line.getOptionValue("workers"));
            }
            
            if (line.hasOption("threads")) {
                threads = Integer.parseInt(line.getOptionValue("threads"));
            }
            
            if (line.hasOption("page")) {
                page = Integer.parseInt(line.getOptionValue("page"));
            }
            
        } catch (NumberFormatException | ParseException e) {
            System.out.println("Invalid arguments: showing -h option");
            printHelp(options);
            throw new IllegalArgumentException();
        }
        
        return true;
        
    }
    
    public int execute(String args[]) {
        try {
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
        catch (IllegalArgumentException e) {
            return EXIT_INVALID_ARGUMENTS_ERROR;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            System.out.println("FAILED: " + e.getMessage());
            return EXIT_ERROR;
        }
        
        return EXIT_SUCCESS;
    }
    
    public static void main(String args[]) {
        CommandLineHandler handler = new CommandLineHandler(new HelpFormatter());
        int result = handler.execute(args);
        System.exit(result);
    }
    
    public String getUrl() {
    
        return url;
    }
    
    public String getAuth() {
    
        return auth;
    }
    
    public String getRepo() {
    
        return repo;
    }
    
    public int getSize() {
    
        return size;
    }
    
    public int getWorkers() {
    
        return workers;
    }
    
    public int getThreads() {
    
        return threads;
    }
    
    public int getPage() {
    
        return page;
    }

}

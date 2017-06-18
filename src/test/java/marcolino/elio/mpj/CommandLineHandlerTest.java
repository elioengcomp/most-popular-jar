package marcolino.elio.mpj;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.Test;

import marcolino.elio.mpj.CommandLineHandler;

public class CommandLineHandlerTest {

    private String[] getArgumentsArray(String args) {
        String argsArray[] = args.split(" ");
        return argsArray;
    }
    @Test
    public void testShowHelp() {
        String[] args = getArgumentsArray("-h");
        HelpFormatter mockedHelpFormatter = mock(HelpFormatter.class);
        CommandLineHandler handler = new CommandLineHandler(mockedHelpFormatter);
        handler.parseArguments(args);
        verify(mockedHelpFormatter).printHelp(anyString(), any(Options.class));
    }

    public void testShowHelpForInvalidArguments(String argsLine) {
        String args[] = getArgumentsArray(argsLine);
        HelpFormatter mockedHelpFormatter = mock(HelpFormatter.class);
        CommandLineHandler handler = new CommandLineHandler(mockedHelpFormatter);
        try {
            handler.parseArguments(args);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {            
        }
        verify(mockedHelpFormatter).printHelp(anyString(), any(Options.class));
    }
    
    @Test
    public void testShowHelpForNoArgs() {
        testShowHelpForInvalidArguments("");
    }
    
    @Test
    public void testShowHelpForNoUrl() {
        testShowHelpForInvalidArguments("-a auth -r repo");
    }
    
    @Test
    public void testShowHelpForNoAuthToken() {
        testShowHelpForInvalidArguments("-u url -r repo");
    }
    
    @Test
    public void testShowHelpForNoRepo() {
        testShowHelpForInvalidArguments("-u url -a auth");
    }
    
    @Test
    public void testShowHelpOnNumberParseError() {
        testShowHelpForInvalidArguments("-u url -a auth -r repo -w text");
    }
    
    @Test
    public void testValidRequiredArguments() {
        String[] args = getArgumentsArray("-u url -a auth -r repo");
        HelpFormatter mockedHelpFormatter = mock(HelpFormatter.class);
        CommandLineHandler handler = new CommandLineHandler(mockedHelpFormatter);
        handler.parseArguments(args);
        assertEquals("url", handler.getUrl());
        assertEquals("auth", handler.getAuth());
        assertEquals("repo", handler.getRepo());
    }
    
    @Test
    public void testValidAllArguments() {
        String[] args = getArgumentsArray("-u url -a auth -r repo -w 2 -t 2 -s 3 -p 100");
        HelpFormatter mockedHelpFormatter = mock(HelpFormatter.class);
        CommandLineHandler handler = new CommandLineHandler(mockedHelpFormatter);
        handler.parseArguments(args);
        assertEquals("url", handler.getUrl());
        assertEquals("auth", handler.getAuth());
        assertEquals("repo", handler.getRepo());
        assertEquals(2, handler.getWorkers());
        assertEquals(2, handler.getThreads());
        assertEquals(3, handler.getSize());
        assertEquals(100, handler.getPage());
    }
    
}

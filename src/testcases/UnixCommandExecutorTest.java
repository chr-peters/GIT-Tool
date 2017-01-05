package testcases;

import application.git_tool.unixcommandexecutor.UnixCommandExecutor;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.*;

import java.util.ArrayList;
import java.util.List;

public class UnixCommandExecutorTest {
    
    @Test
    public void testEcho () {
        ProcessBuilder p = new ProcessBuilder();
        UnixCommandExecutor exec = new UnixCommandExecutor(p);
        
        List<String> cmd = new ArrayList<String>();
        cmd.add("echo");
        cmd.add("Ein Test!");
        
        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("Ein Test!");
        
        List<String> result = exec.executeCommand(cmd);
        
        assertEquals(expectedResult, result);
        assertTrue(exec.getLastExitCode()==0);
    }
    
    @Test
    public void testMkdir () {
        ProcessBuilder p = new ProcessBuilder();
        UnixCommandExecutor exec = new UnixCommandExecutor(p);
        
        //an empty list
        List<String> expectedResult = new ArrayList<String>();
        
        String dirName = "test"+System.currentTimeMillis();
        
        List<String> result = exec.mkdir(dirName);
        
        assertEquals(expectedResult, result);
        assertTrue(exec.getLastExitCode()==0);
        
        List<String> rmCmd = new ArrayList<String>();
        rmCmd.add("rmdir");
        rmCmd.add(dirName);
        
        exec.executeCommand(rmCmd);
        
        assertTrue(exec.getLastExitCode()==0);
    }
    
    public static void main(String[] args) {
        Result rc = new Result();
        
        rc = org.junit.runner.JUnitCore.runClasses(UnixCommandExecutorTest.class);
        
        System.out.println(rc.getRunCount() + " tests were executed, " + rc.getFailureCount() + " of them with failures.");
        
        if( ! rc.wasSuccessful() ) {
            List<Failure> fList = rc.getFailures();
            for ( Failure f: fList ) {
                System.out.println(f.getTestHeader());
                System.out.println(f.getMessage());
            }
        }
    }
    
}

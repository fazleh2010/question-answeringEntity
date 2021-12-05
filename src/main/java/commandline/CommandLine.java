package commandline;




import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class CommandLine {

    public CommandLine(String linkedDataDir, String inputDir, String[] inputFiles, String url, String linkedDataJsonFile) throws Exception {
        Integer index=0;
        for (String file : inputFiles) {
            String commandLine = "curl -X POST -F file=@" + inputDir + file + " -F config=@" + linkedDataJsonFile + " " + url;
            Boolean flag = this.runCommandLine(commandLine);
            index=index+1;
            if (!flag) {
                System.out.println("failed to upload questions::" + file);
                continue;
            } else {
                System.out.println("successfully file::" + file+ " index:"+index+" total:"+inputFiles.length);
            }
        }

        System.out.println("the process is finised now!!");

    }

    public CommandLine(String csvFile) throws Exception {

    }

    public Boolean runCommandLine(String command) throws IOException, InterruptedException {

        Runtime runTime = Runtime.getRuntime();
        //System.out.println("location + scriptName::" + location + scriptName);
        //String[] commands = {"perl", location + scriptName};
        //System.out.println("command::"+command);
        Process process = runTime.exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // Read any errors from the attempted command
        System.out.println("Error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

        if (process.waitFor() == 0) {
            System.err.println("Process terminated ");
            return true;
        } else {
            return false;
        }

    }

    public Boolean runCommandLine(String[] commands) throws IOException, InterruptedException {

        Runtime runTime = Runtime.getRuntime();
        //System.out.println("location + scriptName::" + location + scriptName);
        //String[] commands = {"perl", location + scriptName};
        //System.out.println("command::"+command);
        Process process = runTime.exec(commands);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // Read any errors from the attempted command
        System.out.println("Error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

        if (process.waitFor() == 0) {
            System.err.println("Process terminated ");
            return true;
        } else {
            return false;
        }

    }

    /*public static void main(String[] args) throws IOException, Exception {
        System.out.println("Hallo world!!!");
        String inputDir = "/home/elahi/AHack/questions/load/";
        String outputDir = "/home/elahi/AHack/questions/split/";
        String linkedDataDir = "/home/elahi/AHack/questions/linkedData/";
        String url="https://webtentacle1.techfak.uni-bielefeld.de/quegg/importQuestions";
        String linkedDataJsonFile=linkedDataDir+"dbpedia.json";
        String [] inputFiles=new File(inputDir).list();
        Integer limit=2000;


        for (String inputCsvFile : inputFiles) {
            System.out.println("inputCsvFile:::" + inputCsvFile);
            File inputFile = new File(inputDir + inputCsvFile);
            String[] outputFiles = new File(outputDir).list();
            CsvFile csvFile = new CsvFile(inputFile);
            csvFile.spiltCsv(outputDir, limit);
        }
        
        String [] outputFiles=new File(outputDir).list();
        QueGGUpload questionUpload = new QueGGUpload(linkedDataDir, outputDir, outputFiles, url, linkedDataJsonFile);

      
    }*/


}

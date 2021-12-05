
import commandline.CommandLine;
import constants.ParameterConstant;
import eu.monnetproject.lemon.LemonModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import questions.SparqlQuery;
import uploader.CsvFile;
import util.io.FileUtils;
import static util.io.FileUtils.appendtoFile;

//index location /var/www/html/
@NoArgsConstructor
public class QueGG implements ParameterConstant {

    //private static final Logger LOG = LogManager.getLogger(QueGG.class);
    private static String inputDir = "src/main/resources/test/inputOne/";
    private static String BaseDir = "";
    //private static String outputDir = BaseDir + "src/main/resources/test/output/";
    public static String QUESTION_ANSWER_LOCATION = BaseDir + "questions/";
    public static String QUESTION_ANSWER_FILE = "questions.txt";
    public static String QUESTION_ANSWER_CSV_FILE = "questions.csv";
    public static String ENTITY_LABEL_LIST = "ENTITY_LABEL_LIST";
    public static String CLASS_LOCATION="src/main/resources/class/";;
     public static String CLASS_FILE_NAME="Lexical_Entry_Classes.txt";
     public static String BINDING_FILE_NAME="Binding_Classes.txt";
     public static String BindingQueGGQaldFile="BindingQueGGQaldFile.csv";
      public static String QALD_FILE_NAME="Qald_Classes.csv";
     public static String ENTITY_LOCATION="src/main/resources/entity/";;
    //"1" entity generation
    //"2" entity splitFiles
    //"3" upload files
    // 4 find class//"1" entity generation
    //"2" entity splitFiles
    //"3" upload files
    // 4 find class
     
     //5 get classes of entities of qald

    public static void main(String[] args) throws Exception {
        QueGG queGG = new QueGG();
        String task="5";
        String language="DE";
        String inputDir = "src/main/resources/input"+language+"/";
        String linkedData = "dbpedia";
        linkedData = "src/main/resources/dataset/"+linkedData+"/";
        
        /*if (args.length > 2) {
            System.out.println("taking parameters!!"+args.length);
            language = args[1];
            linkedData=args[2];
            inputDir = "src/main/resources/input" + language + "/";
            linkedData = "src/main/resources/dataset/"+linkedData+"/";
        }*/

        //task=args[0];
        System.out.println("task:::"+task);
        if(task.equals("0")){
            String fileName="src/main/resources/test/allClasses.txt";
            LinkedHashMap<String,String> map=new LinkedHashMap<String,String>();

            map=FileUtils.fileToMap(fileName);
            for(String key:map.keySet()){
                System.out.println(key);

            }
               System.out.println(map.size());
        }
        else if (task.equals("1")) {
            queGG.generateEntitiesEntity(inputDir,linkedData,language);
        }
        else if (task.equals("2")) {
            queGG.splitFiles();
        }
        else if (task.equals("3")) {
            queGG.uploadFiles();
        }
        else if (task.equals("4")) {
            queGG.findEntityLabelGivenClass(CLASS_LOCATION,CLASS_FILE_NAME,BINDING_FILE_NAME,language);
        }
        else if (task.equals("5")) {
            queGG.findClassLabelGivenEntity(ENTITY_LOCATION,QALD_FILE_NAME,linkedData,language);
        }
        else if (task.equals("6")) {
            queGG.joinCsv(CLASS_LOCATION,BindingQueGGQaldFile);
        }

    }

    private void generateEntitiesEntity(String inputDir,String linkedData,String language) {
        String BaseDir = "";
        String outputDir = BaseDir + "src/main/resources/test/output/";
        String ENTITY_LABEL_LIST = "ENTITY_LABEL_LIST";
        String questionAnswerFile = QUESTION_ANSWER_LOCATION + File.separator + QUESTION_ANSWER_CSV_FILE;
        Integer task = 1;
        String content = "";
        String str = "";
        /*for (Integer index = 1950; index < 2020; index++) {
            String line = "http://www.w3.org/2001/XMLSchema#gYear=" + index.toString() + "\n";
            str += line;
            //System.out.println(line);

        }*/
        
        String[] files = new File(inputDir).list();
        Arrays.sort(files);
        System.out.println("files::"+files);
        for (String fileName : files) {
            if (fileName.contains(ENTITY_LABEL_LIST) || fileName.contains(".ttl")) {
                continue;
            }
            String inputFileName = inputDir + fileName;
            String outputFileName = inputDir + ENTITY_LABEL_LIST + "_" + fileName;
            System.out.println("inputFileName::" + inputFileName + " outputFileName:" + outputFileName);
            this.createEntityRdfsLevel(inputFileName, outputFileName, fileName,linkedData,language);
        }
    }
    
      private void findClassLabelGivenEntity(String inputDir,String outputFilename,String linkedData,String language) {
        String BaseDir = "";
        String outputDir = BaseDir + "src/main/resources/test/output/";
        String questionAnswerFile = QUESTION_ANSWER_LOCATION + File.separator + QUESTION_ANSWER_CSV_FILE;
        Integer task = 1;
        String content = "";
        String str = "";
        /*for (Integer index = 1950; index < 2020; index++) {
            String line = "http://www.w3.org/2001/XMLSchema#gYear=" + index.toString() + "\n";
            str += line;
            //System.out.println(line);

        }*/
        
        String[] files = new File(inputDir).list();
        Arrays.sort(files);
        System.out.println("files::"+files);
        for (String fileName : files) {
            if (fileName.contains(ENTITY_LABEL_LIST) || fileName.contains(".ttl")) {
                continue;
            }
            String inputFileName = inputDir + fileName;
            String outputFileName = inputDir + outputFilename;
            System.out.println("inputFileName::" + inputFileName + " outputFileName:" + outputFileName);
            List<String[]>newRows=this.createEntityRdfsLevelClass(inputFileName, outputFileName, fileName,linkedData,language);
            CsvFile CsvFile=new CsvFile(new File(outputFileName));
            CsvFile.writeCSV(newRows);
        }
    }
    
    private static void createEntityRdfsLevel(String inputFileName, String outputFileName,String className,String linkedData,String language) {
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        Integer index=0;
        try {
            reader = new BufferedReader(new FileReader(inputFileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    line = line.strip().trim();
                    line = line.replace("<", "");
                    line = line.replace(">", "");
                    line = line.strip().trim();
                    String entityUrl = line;
                    String sparqlQueryStr = SparqlQuery.setSparqlQueryForLabel(entityUrl,linkedData,language);
                    System.out.println("sparqlQueryStr::"+sparqlQueryStr);
                    SparqlQuery sparqlQuery = new SparqlQuery(sparqlQueryStr,true);
                    System.out.println(sparqlQuery.getObject());
                    if (sparqlQuery.getObject() != null) {
                        System.out.println("className:"+className+" "+index+" entityUrl:" + entityUrl + " object:" + sparqlQuery.getObject());
                        appendtoFile(outputFileName, entityUrl + "=" + sparqlQuery.getObject());
                        //break;
                    }
                }
               index=index+1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private static List<String[]> createEntityRdfsLevelClass(String inputFileName, String outputFileName,String className,String linkedData,String language) {
        List<String[]> rows = new ArrayList<String[]>();
        BufferedReader reader;
        String line = "";
        Integer index=0;
        try {
            reader = new BufferedReader(new FileReader(inputFileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    line = line.strip().trim();
                    line = line.replace("<", "");
                    line = line.replace(">", "");
                    line = line.strip().trim();
                    String entityUrl = line;
                    String entitySparqlStr = SparqlQuery.setSparqlQueryForLabel(entityUrl,linkedData,language);
                    SparqlQuery entitySparql = new SparqlQuery(entitySparqlStr,true);
                    String classSparqlStr=SparqlQuery.setSparqlQueryForClass(entityUrl,language);
                    SparqlQuery classSparql = new SparqlQuery(classSparqlStr,false);
                    Set<String> classes=filterClass(classSparql.getCsvData(),"http://dbpedia.org/ontology/");
                    if (entitySparql.getObject() != null&&!classes.isEmpty()) {
                        System.out.println( index+" entityUrl:" + entityUrl + " object:" + entitySparql.getObject()+" class:"+classes.size());
                        for (String clss : classes) {
                            String[] newRow = new String[3];
                            newRow[0] = entityUrl;
                            newRow[1] = entitySparql.getObject();
                            newRow[2] = clss;
                            rows.add(newRow);
                        }
                    }
                }
               index=index+1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return rows;
    }
    
    private void splitFiles() throws IOException, Exception {
        String inputDir = "/home/elahi/AHack/questions/load/";
        String outputDir = "/home/elahi/AHack/questions/split/";
        String[] inputFiles = new File(inputDir).list();
        Integer limit = 50;

        for (String inputCsvFile : inputFiles) {
            File inputFile = new File(inputDir + inputCsvFile);
            String[] outputFiles = new File(outputDir).list();
            CsvFile csvFile = new CsvFile(inputFile);
            csvFile.spiltCsv(outputDir, limit);
        }

    }

    private void uploadFiles() throws IOException, Exception {
        String outputDir = "/home/elahi/AHack/questions/split/";
        String linkedDataDir = "/home/elahi/AHack/questions/linkedData/";
        String url = "https://webtentacle1.techfak.uni-bielefeld.de/quegg/importQuestions";
        String linkedDataJsonFile = linkedDataDir + "dbpedia.json";
        String[] outputFiles = new File(outputDir).list();
        CommandLine questionUpload = new CommandLine(linkedDataDir, outputDir, outputFiles, url, linkedDataJsonFile);

    }

    private void findEntityLabelGivenClass(String Dir,String inputfileName,String outputfileName,String language) throws IOException {
        List<String> list = FileUtils.fileToList(Dir + File.separator + inputfileName);
        Set<String> classNames = new TreeSet<String>(list);
        Integer index = 0;
        String classEntity = null;
        List<String[]> totalRows = new ArrayList<String[]>();
        CsvFile CsvFile = new CsvFile(new File(Dir + File.separator + outputfileName + ".csv"));


        for (String className : classNames) {

            if (className.contains("dbo:")) {
                className = className.replace("dbo:", "");
            } else {
                continue;
            }

            String sparqlQueryStr = SparqlQuery.setSparqlGivenClass(className, language);
            SparqlQuery sparqlQuery = new SparqlQuery(sparqlQueryStr, false);
            System.out.println("className:" + className);
            List<String[]> dbpediaRows = sparqlQuery.getCsvData();
            if (!dbpediaRows.isEmpty()) {
                List<String[]> newRows = this.addClassName(className, dbpediaRows);
                System.out.println(" " + index + " className:" + className + " entityUrl:" + className + " object:" + newRows.size());
                index = index + 1;
                totalRows.addAll(newRows);
            }
        }
        CsvFile.writeCSV(totalRows);

        //System.out.println("sparqlQueryStr::"+sparqlQueryStr);
    }

    private List<String[]> addClassName(String className, List<String[]> rows) {
        List<String[]> newRows = new ArrayList<String[]>();
        for (String[] row : rows) {
            String[] newRow = new String[3];
            newRow[0] = row[0];
            newRow[1] = row[1].trim().stripLeading();
            newRow[2] = className;
            newRows.add(newRow);
        }
        return newRows;
    }

    private static Set<String> filterClass(List<String[]> csvData, String linkedData) {
        Set<String> classes = new HashSet<String>();

        for (String[] row : csvData) {
            String key = row[0];
            if (key.contains(linkedData)) {
                key=key.replace(linkedData, "");
                classes.add(key);
            }
        }
        return classes;
    }

    private void joinCsv(String inputDir,String bindingFile) {
        List<String[]> totalRows = new ArrayList<String[]>();
        CsvFile CsvFile = new CsvFile(new File(inputDir + File.separator + bindingFile));
        String[] files = new File(inputDir).list();
        for (String file : files) {
            File fileName = new File(file);
            CsvFile localCsvFile = new CsvFile(new File(inputDir + File.separator + fileName));
            List<String[]> rows =localCsvFile.getRows();
            totalRows.addAll(rows);
        }
        CsvFile.writeCSV(totalRows);
    }


}

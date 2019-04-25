package main.java;

import edu.unh.cs.treccar_v2.Data;
import main.java.QueryExpansion.*;
import main.java.ranklib.ranklibTrainer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    static private String INDEX_DIRECTORY = "/Users/xin/Documents/19Spring/DS/index";
    static private String OUTPUT_DIR = "output";
    static final private int Max_Results = 100;
    static private String queryLocation;
    static private String qrelLocation;
    static IndexData indexer;

    public static void main(String[] args) throws Exception,IOException, ParseException {
        System.setProperty("file.encoding", "UTF-8");

        //String queryPath = "/Users/xin/Desktop/benchmarkY2.public/benchmarkY2.cbor-outlines.cbor";
        String queryPath = "/Users/xin/Documents/19Spring/DS/benchmarkY1/benchmarkY1-train/train.pages.cbor-outlines.cbor";

        String dataPath = "/Users/xin/Documents/19Spring/DS/test200/test200-train/train.pages.cbor-paragraphs.cbor";

        String lm_mercer = "lm_mercer";
        String lm_dirichlet = "lm_dirichlet";
        String avg_query = "average_query";
        String split_section = "split_sections";
        String combined = "combined";


        INDEX_DIRECTORY = args[0];
        queryPath = args[1];
        //dataPath = args[1];
        OUTPUT_DIR = args[2];
        //qrelLocation = args[3];

        //indexer = new IndexData(INDEX_DIRECTORY, dataPath);
        //indexer.reIndex();

        QueryData queryData = new QueryData(queryPath);
//
        Map<String,String> pageMap = queryData.getAllPageQueries();
        Map<String,String> sectionMap = queryData.getAllSectionQueries();
        ArrayList<Data.Page> pageList = queryData.getPageList();
        ArrayList<Data.Section> sectionList = queryData.getSectionList();
        System.out.println("Query expansion with wordnet start");
        QueryExpansionWordNet queryExpansionWordNet = new QueryExpansionWordNet(pageMap,sectionMap,INDEX_DIRECTORY,OUTPUT_DIR);
        queryExpansionWordNet.runPage();
        queryExpansionWordNet.runSection();
        System.out.println("Query expansion with wordnet end");

        System.out.println("Query expansion with LTR start");
        QueryExpansionLTR queryExpansionLTR = new QueryExpansionLTR(pageMap,sectionMap,INDEX_DIRECTORY,OUTPUT_DIR);
        queryExpansionLTR.runPage();
        queryExpansionLTR.runSection();
        System.out.println("Query expansion with LTR end");
//

        // Ranklib Query
//             lm-mercer
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + lm_mercer + "_pages_query_results.run", lm_mercer, "pages");
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + lm_mercer + "_sections_query_results.run", lm_mercer, "sections");
//
//            lm_dirichlet
//
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + lm_dirichlet + "_pages_query_results.run", lm_dirichlet, "pages");
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + lm_dirichlet + "_sections_query_results.run", lm_dirichlet, "sections");
//
//         average_query
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + avg_query + "_pages_query_results.run", avg_query, "pages");
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + avg_query + "_sections_query_results.run", avg_query, "sections");
//
//         split sections
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + split_section + "_pages_query_results.run", split_section, "pages");
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + split_section + "_sections_query_results.run", split_section, "sections");
//
//         combined
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + combined + "_pages_query_results.run", combined, "pages");
        runRanklibQuery(INDEX_DIRECTORY, queryPath, OUTPUT_DIR  + combined + "_sections_query_results.run", combined, "sections");
        System.out.println("Finished");
    }

    public static void writeFile(String name, List<String> content){
        String fullpath = OUTPUT_DIR + "/" + name;
        System.out.println(fullpath);
        try (FileWriter runfile = new FileWriter(new File(fullpath))) {
            for (String line : content) {
                runfile.write(line + "\n");
            }

            runfile.close();
        } catch (IOException e) {
            System.out.println("Could not open " + fullpath);
        }
    }

    public static void runRanklibTrainer(String indexPath, String queryLocation, String qrelLocation, String output, String method, String flag) throws IOException {
        ranklibTrainer trainer = new ranklibTrainer(indexPath, queryLocation, qrelLocation, flag);
        trainer.train(method, output);
    }

    public static void runRanklibQuery(String indexPath, String queryLocation, String output, String method, String flag) throws IOException {

        ranklibTrainer trainer = new ranklibTrainer(indexPath, queryLocation, "", flag);
        trainer.runRanklibQuery(method, output);
    }
}

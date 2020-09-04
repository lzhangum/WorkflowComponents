package edu.cmu.pslc.learnsphere.imports.xAPI;

import com.github.opendevl.JFlat;
import edu.cmu.pslc.datashop.workflows.AbstractComponent;        

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImportXAPImain extends AbstractComponent {

    public static void main(String[] args) {
    	ImportXAPImain tool = new ImportXAPImain();
        tool.startComponent(args);
		}

                //Constructor
		public ImportXAPImain() {
		    super();

		}
          
            @Override
	    protected void runComponent() {
                
                File outputDirectory = this.runExternal();
                
                //Get the option parameters
                String lrsUrl=this.getOptionAsString("url")+"statements/";
                String lrsUsername=this.getOptionAsString("username");
                String lrsPassword=this.getOptionAsString("password");
                String queryMode=null; //v2,aggregate
                JSONArray sqlStatements=new JSONArray();
                
                
                //Get the use_customfilter infor.
                JSONObject sqlUrlWithFilterNew = new JSONObject();
                Boolean useCustomfilter=this.getOptionAsBoolean("Use_customfilter");
               if(useCustomfilter){
                   String customfilterCode=this.getOptionAsString("customfilter_code");
                   
                    try {
                        sqlUrlWithFilterNew=new JSONObject(customfilterCode);
                        //JSONObject json = JSONObject.fromObject(str);
                    } catch (JSONException ex) {
                        Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                   //System.out.println(json);
                   //System.out.println(sqlUrlWithFilterNew);
               }else{
                    //If the All_statements=false (by default),it will help us query the specified values in the statements based on the json fields.
                    Boolean All_statements=this.getOptionAsBoolean("All_statements");
                    
                    //Under aggregate mode
                    JSONArray sqlUrlWithFilterAgr = new JSONArray();
                    if(!All_statements){
                        queryMode="usingAggregate";
                        String filterByUntil=this.getOptionAsString("filterByUntil");
                        String filterBySince=this.getOptionAsString("filterBySince");
                        //String group="$statement.verb.id";
                        String matchFilter="statement.timestamp";
                        String matchValue="";
                        String groupingKey="$statement.verb.id";
                        String groupingOperatorValue="";
                        
                        try {
                            try {
                                sqlUrlWithFilterAgr= new filerValuesCombAggregate().sqlUrlWithFilter(filterByUntil, filterBySince, matchFilter, groupingKey);
                            } catch (JSONException ex) {
                                Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        System.out.print(sqlUrlWithFilterAgr);
                        
                        StatementClientVeracity qrlByOptionStsAgr =new StatementClientVeracity();
                        
                        try {
                            sqlStatements=qrlByOptionStsAgr.filterByOptionAgr(sqlUrlWithFilterAgr, queryMode, lrsUrl, lrsUsername, lrsPassword);
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JSONException ex) {
                            Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        
                    }
                    
               }//end of using filter options
                    //System.out.println(sqlUrlWithFilterNew);

                    //System.out.println(sqlStatements.length());
                //System.out.println(sqlStatements);
                    
                File resultFile=null;
                Json2table queryFile=new Json2table();
                try {
                    resultFile=queryFile.nestedJson2csv(sqlStatements, outputDirectory);
                } catch (Exception ex) {
                    Logger.getLogger(ImportXAPImain.class.getName()).log(Level.SEVERE, null, ex);
                }

                Integer nodeIndex0 = 0;
                Integer fileIndex0 = 0;
                String fileType0 = "tab-delimited";
                this.addOutputFile(resultFile, nodeIndex0, fileIndex0, fileType0);
                System.out.println(this.getOutput()); 
	    } 
            
}
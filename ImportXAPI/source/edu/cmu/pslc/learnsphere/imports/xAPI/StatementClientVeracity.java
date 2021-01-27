/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.pslc.learnsphere.imports.xAPI;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *make the logic relations of filter options into query url.
 * @author Liang Zhang
 */
public class StatementClientVeracity {
    public JSONObject sqlUrlWithFilter;
    public JSONArray sqlUrlWithFilterAgr;
    public String queryMode;
    private String lrsUrl;
    private String username;
    private String password;
    
    public JSONArray filterByOptionAgr(JSONArray sqlUrlWithFilterAgr, String queryMode, String lrsUrl, String username, String password) throws UnsupportedEncodingException, MalformedURLException, IOException, JSONException{
        JSONArray stsArray=new JSONArray();  //Statements Array
        
        String urlParameters=sqlUrlWithFilterAgr.toString();
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        String request= lrsUrl+"aggregate/";
        //String request= lrsUrl+"analyze/";
        
        URL url= new URL( request );
        
        String auth1 = username + ":" + password;
        //String encoding = Base64.getEncoder().encodeToString((auth1.getBytes("UTF-8")));
        String encoding=new String(Base64.encodeBase64(auth1.getBytes()));
        
        HttpURLConnection conn= (HttpURLConnection) url.openConnection(); 
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty( "Content-Type", "application/json"); 
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
            wr.flush();
            wr.close();
        }
        
        int responseCode = conn.getResponseCode();
        
        StringBuilder content = null;
        if (responseCode == HttpURLConnection.HTTP_OK){
            System.out.println("GET Response Code :: " + responseCode);
            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    content = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
            }
            
        }

        stsArray = new JSONArray(content.toString());
        
        return(stsArray);
    }
    
   public JSONArray filterByOptionVQL(JSONObject sqlUrlWithFilterVQL, String queryMode, String lrsUrl, String username, String password) throws UnsupportedEncodingException, MalformedURLException, IOException, JSONException{
        JSONArray stsArray=new JSONArray();  //Statements Array
        
        System.out.print(sqlUrlWithFilterVQL.toString());
        
        String encodeDataSql=URLEncoder.encode(sqlUrlWithFilterVQL.toString(), StandardCharsets.UTF_8.toString());
        
        String getDataUrl= lrsUrl+"analyze?query="+encodeDataSql;
        
        //System.out.print(getDataUrl);
        
        String auth = username + ":" + password;
        //byte[] encodedAuth=Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        Base64 base64 = new Base64();
        String encodedAuth = new String(base64.encode(auth.getBytes()));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        System.out.print(authHeaderValue);
        System.out.print(getDataUrl);
        
        //set the request method and properties using HttpURLConnection
        URL obj=new URL(getDataUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", authHeaderValue);
        
        int responseCode=con.getResponseCode();
        //System.out.print(responseCode);
        
        if (responseCode == HttpURLConnection.HTTP_OK){
            //System.out.println("GET Response Code :: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
            String inputLine;
            StringBuffer sts = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                    sts.append(inputLine);
            }
            in.close();
            stsArray = new JSONArray(sts.toString()); 
        }else{
            System.out.println("GET request not worked");
        }
        
        return(stsArray);
    }
    
    public JSONArray filterByOption(JSONObject sqlUrlWithFilter,String queryMode,String lrsUrl, String username, String password) throws JSONException, MalformedURLException, IOException {    
        JSONArray stsArray=new JSONArray();  //Statements Array
        //create the query link
        String encodeDataSql=URLEncoder.encode(sqlUrlWithFilter.toString(), StandardCharsets.UTF_8.toString());
        String getDataUrl=null;
        if(queryMode.equals("usingAggregate")){
            getDataUrl=lrsUrl+"aggregate"+"search?"+"mode=v2"+"&query="+encodeDataSql;
        }else{
            getDataUrl=lrsUrl+"search?"+"mode=v2"+"&query="+encodeDataSql;
        }
        
        //String getDataUrl=lrsUrl+"search?query="+encodeDataSql;
        String auth = username + ":" + password;
        //byte[] encodedAuth=Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8)); 
        Base64 base64 = new Base64();
        String encodedAuth = new String(base64.encode(auth.getBytes()));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        
        //set the request method and properties using HttpURLConnection
        URL obj=new URL(getDataUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", authHeaderValue);
        
        int responseCode=con.getResponseCode();
        
        if (responseCode == HttpURLConnection.HTTP_OK){
            System.out.println("GET Response Code :: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
            String inputLine;
            StringBuffer sts = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                    sts.append(inputLine);
            }
            in.close();
            stsArray = new JSONArray(sts.toString()); 
        }else{
            System.out.println("GET request not worked");
        }
       return(stsArray);         
    }
     
}

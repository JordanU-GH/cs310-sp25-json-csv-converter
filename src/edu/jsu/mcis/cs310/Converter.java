package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

// New Imports Jordan added
import java.io.StringReader;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.io.StringWriter;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // INSERT YOUR CODE HERE
            // create a jsonObject to hold records
            JsonObject records = new JsonObject();
            // create the necessary data structures to hold the values for the records
            ArrayList<String> prodNums = new ArrayList<>();
            ArrayList<String> headings = new ArrayList<>();
            ArrayList<Object[]> data = new ArrayList<>();
            // create a reader and iterator so we can split the information appropriately
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            List<String[]> full = csvReader.readAll();
            Iterator<String[]> iterator = full.iterator();
            // appropriately split the information from the input data
            if(iterator.hasNext()){
                // place first line into headings
                String[] line = iterator.next();
                for(String s : line){
                    headings.add(s);
                }
                // while there are more lines, place the prodNums into prodNums and the 
                // remaining data into data
                while(iterator.hasNext()){
                    ArrayList<Object> temp = new ArrayList<>();
                    line = iterator.next();
                    prodNums.add(line[0]);
                    for(int i = 1; i < line.length; i++){
                        if( (i == 2) || (i == 3)){
                            temp.add(Integer.valueOf(line[i]));
                        } else{
                            temp.add(line[i]);
                        }
                    }
                    data.add(temp.toArray());
                }
                records.put("ProdNums", prodNums);
                records.put("ColHeadings", headings);
                records.put("Data", data);
            }
            
            
            // serialize the records for output
            result = Jsoner.serialize(records);
            csvReader.close();
            /*  **** Code for Testing Output ****
            System.out.println(result);
            InputData input = new InputData();
            System.out.println(input.getJsonString());
                **** End of testing code **** */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // INSERT YOUR CODE HERE
            // create a CSVWriter to compose the data for the CSV file
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            // deserialize the jsonString data and place it into a container
            JsonObject jsonStr = Jsoner.deserialize(jsonString, new JsonObject());
            // create data structures to store data from the jsonStr
            ArrayList<String> headings = new ArrayList<>();
            ArrayList<String> prodNums = new ArrayList<>();
            ArrayList<Object> data = new ArrayList<>();
            // pull data from jsonStr and place it into the appropriate data structures
            /*// ** code that kind of works ** */
            for(Map.Entry<String, Object> entry : jsonStr.entrySet()){
                String key = entry.getKey();
                Object value = entry.getValue();
                if(value instanceof JsonArray){
                   JsonArray array = (JsonArray) value;
                   for(Object item : array){
                       if (key.equals("ProdNums")){
                           prodNums.add(item.toString());
                       }
                       else if (key.equals("ColHeadings")){
                           headings.add(item.toString());
                       }
                       else if (key.equals("Data")){
                           data.add(item);
                       }
                   }
                }
            }
            // ** End of code that kind of works ** */
            // convert data structures into string arrays for the csvWriter to use
            String[] headingArray = new String[headings.size()];
            for(int i = 0; i < headings.size(); i++){
                headingArray[i] = headings.get(i);
            }
            String[] prodNumArray = new String[prodNums.size()];
            for (int i = 0; i < prodNums.size(); i++){
                prodNumArray[i] = prodNums.get(i);
            }
            // write the headings to our csvWriter
            csvWriter.writeNext(headingArray);
            // combine the prodNums with the correct data and write it to the csvWriter
            for(int i = 0; i < prodNumArray.length; i++){
                String[] tempArray = new String[headingArray.length];
                JsonArray dataVals = (JsonArray) data.get(i);
                tempArray[0] = prodNumArray[i];
                // split the lists in the data Arraylists into their individual parts to be paired with prodNums
                for(int j = 0; j < dataVals.size(); j++){
                    Object tempObj = dataVals.get(j);
                    // determine if the current data entry location is the episode number
                    if (j == 2){
                        Number tempNum = dataVals.getBigDecimal(j);
                        // adjust format for the episode number
                        if (tempNum.intValue() < 10){
                            tempArray[j+1] = "0" + tempObj.toString();
                        }
                        else{
                            tempArray[j+1] = tempObj.toString();
                        }
                    } 
                    else{
                        tempArray[j+1] = tempObj.toString();
                    }
                }
                csvWriter.writeNext(tempArray);
            }
            // convert the writer to a string for output
            csvWriter.close();
            result = writer.toString();
            /* // ***** Code for testing output *****
            for(String s : headingArray){
                System.out.print(s + " ");
            }
            System.out.println();
            for(int i = 0; i < prodNumArray.length; i++){
                System.out.println(prodNumArray[i] + data.toArray()[i].toString());
            }
            // ***** End of Output Test ***** */
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}

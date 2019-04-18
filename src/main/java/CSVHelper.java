import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private String filePath;
    private List<String[]> list;

    public CSVHelper(){
        filePath = "";
        list = new ArrayList<String[]>();
    }

    public CSVHelper (String s, List<String[]> l){
        this.filePath = s;
        this.list = l;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String[]> getList() {
        return list;
    }

    public void setList(List<String[]> list) {
        this.list = list;
    }

    public boolean GenerateCSV (){
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        for(String[] s : list)
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(list);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }return true;
    }
}

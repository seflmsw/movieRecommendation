package movieRecommendation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args){

        List<List<String>> csv = getCSV();

        System.out.println(csv);
    }

    public static List<List<String>> getCSV(){
        List<List<String>> ret = new ArrayList<List<String>>();
        BufferedReader br = null;

        try{
            br = Files.newBufferedReader(Paths.get("C:\\Users\\luraw\\OneDrive\\Desktop\\data\\movies_metadata.csv"));
            //Charset.forName("UTF-8");
            String line = "";

            while((line = br.readLine()) != null){
                //CSV 1행을 저장하는 리스트
                List<String> tmpList = new ArrayList<String>();
                String array[] = line.split(",");

                tmpList = Arrays.asList(array);
                //System.out.println(tmpList);
                ret.add(tmpList);
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(br != null){
                    br.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return ret;
    }
}

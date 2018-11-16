import java.io.*;
import java.util.*;

/*
   Author: Siyu Chen
   CWID: 10424481
   Cite: Yuhao Zhu
   Comment: I learned from Yuhao Zhu and I do appreciate for his assistance !!!
 */

public class Parsefile{
    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("Frankenstein.txt"));
        String content = "";
        ArrayList<String> array = new ArrayList<String>();
        LinkedHashMap<String,Integer> common = new LinkedHashMap<>();
        LinkedHashMap<String,Integer> wordCount = new LinkedHashMap<>();
        Scanner br1 = new Scanner(new BufferedReader(new FileReader("shortwords.txt")));
        while(br1.hasNext()){
            array.add(br1.next());    
        }
        for(int i = 0;i < array.size(); i++){
            common.put(array.get(i),0);   
        }
        while((content=br.readLine())!=null){
            String regex = "[ \\.\\?\\,\\:\\;\\!]+";
            String[] content1 = content.split(regex);
            for(String c:content1){
                System.out.println(c + " ");
                if(common.containsKey(c)){
                    Integer count = common.get(c);
                    count++;
                    common.put(c,count);
                }
            }
        }
        for(String key :common.keySet()){
            System.out.println("The occurency of"+" <"+key+"> "+"is "+ common.get(key));
        }
        br.close();
        BufferedReader br2 = new BufferedReader(new FileReader("Frankenstein.txt"));
        ArrayList<String> array1 = new ArrayList<String>();
        while((content=br2.readLine()) != null){
            String[] content1 = content.split("[ \\.\\?\\,\\:\\;\\!]+");
            for(int i=1;i<content1.length;i++){
                array1.add(content1[i]);
            }
        }
        String[] array1_str = (String[]) array1.toArray(new String[0]);
        for(String key : common.keySet()){
            ArrayList<String> before_after = new ArrayList<>();
            for(int i=0; i < array1_str.length; ++i){
                if(key.equals(array1_str[i])){
                    before_after.add(array1_str[i - 1]);
                    before_after.add(array1_str[i + 1]);
                }
            }
            for(String ba : before_after){
                if(wordCount.containsKey(ba)){
                    wordCount.put(ba,wordCount.get(ba)+1 );
                }
                else
                    wordCount.put(ba,1);
            }
            for(String key1:wordCount.keySet()){
                if( wordCount.get(key1) >= 50){
                    System.out.println("The word before or after "+key+" "+"<"+key1 +">"+ "==>" + wordCount.get(key1));
                }
            }
        }
    }
}
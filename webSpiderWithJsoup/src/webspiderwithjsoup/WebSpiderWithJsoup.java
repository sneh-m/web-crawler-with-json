/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webspiderwithjsoup;

import java.util.*;
import java.sql.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Sneh Kumar Mishra
 */
class DatabaseControl{
    Connection con = null;
    Statement smt = null;
    DatabaseControl(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/KIITQS","sneh", "mypass");
            
            smt = con.createStatement();
            String query = "CREATE TABLE if NOT EXISTS pdfFile(sl int auto_increment primary key, name varchar(700), searchKey VARCHAR(700), link varchar(700) unique, branch varchar(20))";
            smt.execute(query);
            System.out.println("Table created!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    void addFile(String key, String branch, String name, String link){
        try{
        smt.execute("insert into pdfFile (name, link, branch, searchKey) values('"+name+"','"+link+"','"+branch+"','"+key+"')");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 
}

class ScrapingControl{
    
    HashSet<String> visitedLinks = null;
    DatabaseControl db = null; 
    String parent;
    String mustStartWith;
    String branch; 
    
    ScrapingControl(){
        visitedLinks = new HashSet<String>();
        db = new DatabaseControl(); 
    }
    
    void startNew(String b, String s, String c){
        parent = s;
        mustStartWith = c;
        branch = b;
        start(s);
    }
    
    void searchFile(String url){
        try{
            if(!visitedLinks.contains(url)){
                visitedLinks.add(url);
                Document doc = Jsoup.connect(url).get();
                
                Elements files = doc.select("a[href$=.pdf]");
                for(Element f : files){
                    if(!visitedLinks.contains(f.attr("abs:href"))){
                        String key = processKey(f.text());
                        db.addFile(key ,branch,f.text(), f.attr("abs:href"));
                        visitedLinks.add(f.attr("abs:href"));
                    }
                }  
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    void start(String url){
        try{
            if(!visitedLinks.contains(url)){
                visitedLinks.add(url);
                Document doc = Jsoup.connect(url).get();
                
                Elements links = doc.select("a[href]");
                for(Element l : links){
                    String newLink = l.attr("abs:href");
                    if(!visitedLinks.contains(newLink) && (newLink.length()>=parent.length()) && parent.equals(newLink.substring(0, parent.length()))){
                        System.out.println(l.text());
                        start(newLink);
                    }
                    else if((newLink.length()>mustStartWith.length()) && mustStartWith.equals(newLink.substring(0, mustStartWith.length()))){
                        searchFile(newLink);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    String processKey(String s){
        String temp = "";
        for(int i=0; i<s.length(); i++){
            if( (s.charAt(i) <= 'Z')&& (s.charAt(i) >= 'A') ){
                temp += (char)s.charAt(i);
            }
            else if( (s.charAt(i) <= 'z') && (s.charAt(i) >= 'a') ){
                temp += (char)(s.charAt(i)-32);
            }
            else if( (s.charAt(i) <= '9') && (s.charAt(i) >= '0') ){
                temp += (char)(s.charAt(i));
            }
        }
        
        return temp;
    }
}
public class WebSpiderWithJsoup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            ScrapingControl sc = new ScrapingControl();
            
            /* CSE */
            sc.startNew("cse","http://10.2.0.80:8080/dspace/handle/123456789/32?offset=","http://10.2.0.80:8080/dspace/handle/123456789/" ); 
            
            /* civil */
            sc.startNew("civil","http://10.2.0.80:8080/dspace/handle/123456789/182?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /* electrical engineering*/
            sc.startNew("ee","http://10.2.0.80:8080/dspace/handle/123456789/151?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /*Electronics */
            sc.startNew("etc","http://10.2.0.80:8080/dspace/handle/123456789/717?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /*mechanical */
            sc.startNew("mech.","http://10.2.0.80:8080/dspace/handle/123456789/15?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /* humanities */
            sc.startNew("humanities","http://10.2.0.80:8080/dspace/handle/123456789/29?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /* Applied Science */
            sc.startNew("app","http://10.2.0.80:8080/dspace/handle/123456789/189?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
            
            /* 1st year */
            sc.startNew("1st","http://10.2.0.80:8080/dspace/handle/123456789/1295?offset=", "http://10.2.0.80:8080/dspace/handle/123456789/");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

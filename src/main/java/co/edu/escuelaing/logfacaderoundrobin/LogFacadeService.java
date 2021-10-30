package co.edu.escuelaing.logfacaderoundrobin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
/**
 *
 * @author dtorres
 */
public class LogFacadeService {
    
    static int currentService=0;
    static List<String> serviceList = new ArrayList();
    
    public static void main(String [] args){
        
        serviceList.add("http://dockercontainerlogservice1:4569/logmsg?msg=");
        serviceList.add("http://dockercontainerlogservice2:4569/logmsg?msg=");
        serviceList.add("http://dockercontainerlogservice3:4569/logmsg?msg=");
        staticFiles.location("/public");
        
        port(4000);
        get("/logfacade",(req,res) -> roundRobinFacadeDelegation(req,res));
                
    }
    
    public static String roundRobinFacadeDelegation(Request req, Response res){
        
        System.out.println("Servicio: " + serviceList.get(currentService));
        String resp = callDelegatedService(serviceList.get(currentService), req, res);
        currentService ++;
        if(currentService > 2){
            currentService =0;
        }
        return resp;
        
    }
        
       
        public static String callDelegatedService(String urlStr, Request req, Response res){
        
        try {
            String serviceUrlStr = urlStr;
            serviceUrlStr = serviceUrlStr + req.queryParams("msg");
            URL serviceUrl = new URL(serviceUrlStr);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(serviceUrl.openStream()));

            String inputLine;
            String resp = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                resp = resp + inputLine;
            }
            in.close();  
            
            return resp;
        } catch (MalformedURLException ex) {
            Logger.getLogger(LogFacadeService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogFacadeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
        
    }
        
        
 }
    


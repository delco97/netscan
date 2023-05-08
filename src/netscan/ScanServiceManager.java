package netscan;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import static netscan.ScanIpManager.scanned;

/**
 * Classe che Gestisce lo scan dei servizzi di rete
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class ScanServiceManager {
    private int nThread;
    private String ip_urlTarget;
    private ArrayList<TServiceScan> scanners;
    private int timeout;
    private static StylePanelMessages report=null;
    private static JLabel jLabel_progres=null;
    private static Map<String,String> portaDescription=null;//porta,descrizione
    static int scanned=0;
    static boolean scanning=false;
    static int nService=0;
    static final int NUMPORTE=65536;
    /**
     * Costruttore della classe ScanServiceManager
     * @param ip_url Stringa che conterrà l'indirizzo IP
     * @param nThread vaiabile intera che rappresenterà il numero di threads che verranno usati
     * @param report Oggetto di tipo StylePanelMessages
     * @param jLabel_progres oggetto di tipo JLabel
     * @param timeout variabile intera che rappresenteà il tempo di timeout 
     */
    public ScanServiceManager(String ip_url,int nThread,StylePanelMessages report,JLabel jLabel_progres,int timeout){
      this.nThread=nThread;
      ip_urlTarget=ip_url;
      scanners = new ArrayList<TServiceScan>();
      this.report=report;
      this.jLabel_progres=jLabel_progres;
      this.timeout=timeout;
      scanned=0;
      nService=0;
      scanning=false;      
      //Carico file di riferimento per la descrizione delle porte
      loadPortDescription("porte.csv");
      //Distribuzione del lavoro
      int portToScanEachThread=NUMPORTE/nThread;
      int da=0;
      int a=da+portToScanEachThread;
      for(int i=0;i<nThread;i++){
        scanners.add(new TServiceScan(ip_urlTarget,da,a,report,timeout));
        System.out.println("Thread "+i+"["+da+","+a+"]");
        da=a+1;
        if(i==nThread-2)//preparazione intervallo per Ultimo thread
            a+=portToScanEachThread-2;
        else
          a+=portToScanEachThread;
      }        
    }
    /**
     * Metodo che ritorna il servizio di rete in una porta 
     * @param porta variabile intera che indica la porta che viene analizzata
     * @return ritorna una stringa contenente la descrizione della porta
     */
    synchronized static String portDescription(int porta){
      String str_porta=Integer.toString(porta);
      return portaDescription.get(str_porta);
    }
    /**
     * Metodo che controlla se lo scan è in corso
     * @return  ritorna un boolean. true se lo scan è in corso false se non lo è
     */
    public boolean isScanning(){return scanning;}
    /**
     * Metodo che stoppa lo scan 
     */
    public void interrupt(){
      for(int i=0;i<scanners.size();i++){
            scanners.get(i).stopRun();
      }
      scanning=false;
      report.appendChatMessage("Scansione interrotta","Sono stati trovati "+nService+" servizzi attivi.",Color.red,Color.BLACK);
    }
    /**
     * 
     * @param fName
     * @return 
     */
    private boolean loadPortDescription(String fName){
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
        int porta = 80;
        String strPort=Integer.toString(porta);
        portaDescription = new HashMap<String, String>();
	try {
          InputStream input = this.getClass().getResourceAsStream(fName);
          InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
          br = new BufferedReader(isr);
	  while ((line = br.readLine()) != null) {
            // use comma as separator
	    String[] port = line.split(cvsSplitBy);
            portaDescription.put(port[1],port[0]);
          }
	} catch (FileNotFoundException e) {	
            e.printStackTrace();
            return false;
	} catch (IOException e) {
	  e.printStackTrace();
          return false;
	} finally {
	  if (br != null) {
	    try {
	      br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
          }
        }
        return true;
    }
    /**
     * Metodo che controlla se lo scan sulla porta è stato effettuato
     * @param open variabile booleana che indica se la porta era aperta o meno. true se era aperta false se non lo era
     */
    synchronized static void scanPortaEffettuato(boolean open){
      if(scanning){
        scanned++;
        if(open)nService++;
        double percent = Math.ceil((double)scanned/(double)(NUMPORTE-1)*100);
        jLabel_progres.setText(scanned+" su "+(NUMPORTE-1)+" ("+percent+"%) - "+nService+" servizi trovati.");
        System.out.println("Scanned: "+scanned);
        if(scanned==NUMPORTE-1){//Fine scansione
          report.appendChatMessage("Fine scansione","Sono stati trovati "+nService+" servizzi attivi.",Color.green,Color.BLACK);
          scanning=false;
        }
      }
    }
    /**
     * Metodo che effettua il ping sulla rete
     * @param strIp Stringa che conterrà l'indirizzo IP su cui fare il ping
     * @return  ritorna una variabile booleana. ritorna true se il ping è stato effettuato false se non lo è
     */ 
    public boolean runPingCommand(String strIp){
        String s = "";
        try {
            //System.out.println("Ping to: "+strIp);
            long startTime = System.nanoTime();
            Process p = Runtime.getRuntime().exec("ping "+strIp+" -n 1");
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000;             
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            // reading output stream of the command
            String aux="";
            while ((aux= inputStream.readLine()) != null) {
                //System.out.println(s);
                s+=aux+"\n";
            }
            //System.out.println(s);
            if(s.contains("Impossibile") || s.contains("Richiesta scaduta")){//Host non trovato
              //report.appendChatMessage("SCAN "+strIp+": ", "impossibile raggiunger l'host", Color.RED, Color.BLACK);
              return false;
            }
            else{
              //report.appendChatMessage("SCAN "+strIp+": ", "host individuato in "+duration+" ms", Color.GREEN, Color.BLACK);
              return true;
            }

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Ping fallito!");
            return false;
        }
    }
    /**
     * Metodo che fa iniziare lo scan dei servizzi di rete
     */
    public void startScan(){ 
      report.appendChatMessage("Ricerca di "+ip_urlTarget+ " in corso...","", Color.black, Color.black);  
      if(runPingCommand(ip_urlTarget)){
        report.appendChatMessage(ip_urlTarget+ " trovato.","", Color.green, Color.black);    
        report.appendChatMessage("Scansione delle porte in corso...","", Color.black, Color.black);  
        scanned=0;
        nService=0;
        scanning=true;
        for(int i=0;i<scanners.size();i++){
          scanners.get(i).start();
        }
      }
      else{
        report.appendChatMessage("Scansione fallita:", " impossibile raggiungere "+ip_urlTarget, Color.red, Color.black);
      }
    }
}

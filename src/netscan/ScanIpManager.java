package netscan;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Gestisce lo scan di un singolo indirizzo ip o di una rete
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class ScanIpManager {
    private int nThread;
    private int bitPrefisso;
    private ArrayList<TPing> scanners;
    private int timeout;
    static int nAllValidIp;
    static int scanned;
    static int hostFound;
    static boolean scanning;
    static private StylePanelMessages report=null;
    static JLabel jLabel_progres=null;
    /**
     * Costruttore della classe ScanIpManager
     * @param bitPrefisso  variabile intera che indicherà il prefisso di rete nello scan di tutta la rete
     * @param strIp Stringa che conterrà l'indirizzo di rete
     * @param nThread variabile intera che conterrà il numero di thread che verranno usati
     * @param report Oggetto di tipo StylePanelMessages
     * @param jLabel_progres Oggetto di tipo JLabel
     * @param timeout variabile intera che indicherà il tempo di timeout 
     */
    public ScanIpManager(int bitPrefisso,String strIp,int nThread,StylePanelMessages report,JLabel jLabel_progres,int timeout){//Scan di rete
      this.bitPrefisso=bitPrefisso;
      this.nThread=nThread;
      this.report=report;
      this.jLabel_progres=jLabel_progres;
      nAllValidIp=(int)Math.pow(2, 32-bitPrefisso);
      scanners = new ArrayList<TPing>();
      this.timeout=timeout;
      scanned=0;
      hostFound=0;
      scanning=false;
      //Distribuzione del lavoro fra n thread
      int ipToScanEachThread=nAllValidIp/nThread;
      int da=0;
      int a=da+ipToScanEachThread;      
      for(int i=0;i<nThread;i++){
        scanners.add(new TPing(strIp,bitPrefisso,da,a,report,timeout));
        System.out.println("Thread "+i+"["+da+","+a+"]");
        da=a+1;
        if(i==nThread-2)//preparazione intervallo per Ultimo thread
            a+=ipToScanEachThread-2;
        else
          a+=ipToScanEachThread;
      }
    }
    /**
     * metodo che controlla se il ping è stato effettuato
     * @param found variabile booleana che indica se un host è stato trovato o meno. true è stato trovato false no
     */
    synchronized static void pingEffettuato(boolean found){
      if(scanning){
        scanned++;
        if(found)hostFound++;
        double percent = Math.ceil((double)scanned/(double)(nAllValidIp-1)*100);
        jLabel_progres.setText(scanned+" su "+(nAllValidIp-1)+" ("+percent+"%) - "+hostFound+" host trovati.");
        System.out.println("Scanned: "+scanned);
        if(scanned==nAllValidIp-1){//Fine scansione
          report.appendChatMessage("Fine scansione: ","Sono stati trovati "+hostFound+" host.",Color.green,Color.BLACK);
          scanning=false;
        }
      }
    }
    /**
     * Metodo che fa iniziare lo scan
     */
    public void startScan(){
        scanned=0;
        hostFound=0;
        scanning=true;
        report.appendChatMessage("Scansione in corso...","",Color.black,Color.BLACK);
        for(int i=0;i<scanners.size();i++)
            scanners.get(i).start();
    /**
     * Metodo che stoppa lo scan 
     */ 
    }
    public void interrupt(){
        for(int i=0;i<scanners.size();i++){
            scanners.get(i).stopRun();
        }
        scanning=false;
        report.appendChatMessage("Scansione interrotta: ","Sono stati trovati "+hostFound+" host.",Color.red,Color.BLACK);
    }
    /**
     * Metodo che controlla se lo scan è in corso
     * @return ritorna un boolean. true se lo scan è in corso false se non lo è
     */
    public boolean isScanning(){return scanning;}
    /**
     * Metodo che ritorna il numero possibili di host presenti
     * @return ritorna una variabile intera che rappresenta il numro di host presenti
     */
    public int getNumPossibleHost(){return nAllValidIp;}
}

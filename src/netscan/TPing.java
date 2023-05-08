package netscan;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Classe che gestisce il ping con i threads
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class TPing extends Thread{

  private int bitPrefisso;//8,16,24
  private int nByteBloccati;//prefisso/8;
  private String ipPrefisso;//prefisso scelto.
  private String ipToScan;
  private boolean singleHost;
  private int timeout;
  private int from;//dalla macchina from (da convertire in ip)
  private int to;//alla macchina to
  private StylePanelMessages report;//Pannelo di report dei messaggi di scan
  private boolean running;
  
  /**
   * Costruttore della classe TPing
   * @param str_ip stringa che contiene l'indirizzo IP
   * @param prefisso inetro che contiene il prefisso di rete
   * @param from intero che indica la macchina da cui partire per fare il ping 
   * @param to intero che indica la macchina a cui arrivare col ping
   * @param report oggetto di tipo StylePanelMessages
   * @param timeout intero che indica il tempo di timeout
   */
  public TPing(String str_ip,int prefisso,int from,int to,StylePanelMessages report,int timeout){//Scan di un range di IP
    nByteBloccati = prefisso/8;
    bitPrefisso = prefisso;
    this.report=report;
    singleHost=false;  
    this.from=from;
    this.to=to;
    this.timeout=timeout;
    String[] aux=str_ip.split("\\.");
    ipPrefisso="";
    for(int i=0;i<nByteBloccati;i++){
      ipPrefisso+=aux[i]+".";    
    }
    
  }
  /**
   * Costruttore della classe TPing
   * @param str_ip stringa che contiene l'indirizzo IP
   * @param report oggetto di tipo StylePanelMessages
   * @param timeout intero che indica il tempo di timeout
   */
  public TPing(String str_ip,StylePanelMessages report,int timeout){//Scan di un singolo IP
    ipToScan=str_ip;
    this.report=report;
    singleHost=true;
    this.timeout=timeout;
  }  
  
  @Override
  public void run() {
    if(singleHost)runPingCommand(ipToScan);
    else scanRete();
  }
  /**
   * Trasforma l'indirizzo IP da una variabile intera ad una stringa 
   * @param ip variabile intera che rappresenta l'IP
   * @return ritorna una stringa che contiene l'indirizzo IP
   */
  private String integerToStringIP(int ip) {
        return ((ip >> 24 ) & 0xFF) + "." +

               ((ip >> 16 ) & 0xFF) + "." +

               ((ip >>  8 ) & 0xFF) + "." +

               ( ip        & 0xFF);
  }
  /**
   * Metodo che fa lo scan di rete
   */
  private void scanRete(){
      //N.B devo escludere l'ultimo ed il primo indirizzo ip dallo scan dato che sono l'ip
      //    di rete e di brodcast
        running=true; 
        for(int i=from;i<=to && running;i++){//System.out.println("Thread ["+from,+"[ calculatedIp "+i+": "+integerToStringIP(i));
          String[] auxIp= integerToStringIP(i).split("\\.");
          String ip="";
          ip+=ipPrefisso;;
          for(int j=nByteBloccati;j<auxIp.length-1;j++)ip+=auxIp[j]+".";
          ip+=auxIp[auxIp.length-1];
          System.out.println("Scan:"+ip);
          runPingCommand(ip);
        }

  }
  /**
   * Metodo che ferma il ping
   */
  public void stopRun(){running=false;}
  /**
   * Metodo che fa partire il ping utilizzando il pjng di Windows 
   * @param strIp stringa checontiene l'indirizzo IP
   */
  public void runPingCommand(String strIp){
        String s = "";
        try {
            //System.out.println("Ping to: "+strIp);
            long startTime = System.nanoTime();
            Process p = Runtime.getRuntime().exec("ping "+strIp+" -n 1 -w "+timeout);
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
            System.out.println(s);
            if(s.contains("Impossibile") || s.contains("Richiesta scaduta")){//Host non trovato
              report.appendChatMessage("SCAN "+strIp+": ", "impossibile raggiunger l'host", Color.RED, Color.BLACK);
              if(!singleHost)ScanIpManager.pingEffettuato(false);
            }
            else{
              report.appendChatMessage("SCAN "+strIp+": ", "host individuato in "+duration+" ms", Color.GREEN, Color.BLACK);
              if(!singleHost)ScanIpManager.pingEffettuato(true);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            report.appendChatMessage("SCAN "+strIp+": ", "impossibile raggiunger l'host", Color.RED, Color.BLACK);
        }
    }  
  
}

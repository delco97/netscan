package netscan;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Classe che fa lo scan dei servizzi utilizzando i threads
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class TServiceScan extends Thread{
    
    
    private String ip_url;
    private int daPorta;
    private int aPorta;
    private StylePanelMessages report;
    private int timeout;
    private final int NUMPORTE=65536;
    private boolean running;
    /**
     * Costruttore della classe TServiceScan
     * @param ip_url Stringa che contiene l'indirizzo IP
     * @param da intero che rappresenta la macchina da cui iniziare lo scan
     * @param a intero che rappresenta la macchina dove finire lo scan
     * @param report oggetto di tipo StylePanelMessages
     * @param timeout intero che indica il tempo di timeout
     */
    public TServiceScan(String ip_url,int da,int a,StylePanelMessages report,int timeout){
      this.ip_url=ip_url;
      daPorta=da;
      aPorta=a;        
      this.report=report;
      this.timeout=timeout;
    }
      
  @Override
  public void run() {
      //if(runPingCommand(ip_url))
      scanPorte();
  }
  /**
   * Metodo che esegue lo scan delle porte
   */
  private void scanPorte(){
      running=true; 
      for(int i=daPorta;i<=aPorta && running;i++){//Tento di aprire una socket sulla porta i
          try{//Tentativo di connessione TCP
            Socket conSocket = new Socket();
            conSocket.connect(new InetSocketAddress(ip_url, i), timeout);
            System.out.println("Porta: "+i+" aperta");
            String desc = ScanServiceManager.portDescription(i);
            report.appendChatMessage("Porta "+i+" aperta: ", desc, Color.black, Color.black);
            conSocket.close();
            ScanServiceManager.scanPortaEffettuato(true);
          }
          catch(Exception e){
            //JOptionPane.showMessageDialog(, "Connessione al server fallita","Attenzione", JOptionPane.ERROR_MESSAGE);        
            ScanServiceManager.scanPortaEffettuato(false);
          }
      }
      
  }
  /**
   * Metodo che stoppa la ricerca dei servizzi di rete
   */
  public void stopRun(){running=false;}
   
    
}

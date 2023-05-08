package netscan;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import javax.swing.*;

/**
 *Classe che definisce lo stile nei messaggi nel jPanel
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class StylePanelMessages {
    protected JTextPane panel;
    
    
    /**
     * Costruttore della classe StylePanelMessages
     * @param p oggetto di tipo JTextPane
     */
    public StylePanelMessages(JTextPane p){
      panel = p;
    }
    /**
     * Metodo che stampa messaggi sul jPanel
     * @param header Stringa che dichiara che la scansione è stata interrotta
     * @param msg Stringa che contiene il messaggio da dare
     * @param headerColor oggetto di tipo Color che imposterà il colore al header
     * @param contentColor oggetto di tipo Color che imposterà il colore al msg
     */
    synchronized public void appendChatMessage(String header,String msg,Color headerColor, Color contentColor){
        if(panel!=null){
        panel.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        panel.setEditable(false);
        }
    }
    /**
     * Metodo che imposterà il messaggio di testa
     * @param header Stringa che conterrà il messaggio
     * @param color oggetto Color che darà il colore al messaggio
     */
    private void getMsgHeader(String header, Color color){
        int len = panel.getDocument().getLength();
        panel.setCaretPosition(len);
        panel.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Impact", 13), false);
        panel.replaceSelection(header);
    }
    /**
     * Metodo che imposterà il messaggio 
     * @param msgStringa che conterrà il messaggio
     * @param color oggetto Color che darà il colore al messaggio
     */
    private void getMsgContent(String msg, Color color){
        int len = panel.getDocument().getLength();
        panel.setCaretPosition(len);
        panel.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Arial", 12), false);
        panel.replaceSelection(msg +"\n\n");
    }           
}

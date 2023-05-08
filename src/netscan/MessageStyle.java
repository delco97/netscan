package netscan;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Classe che definisce lo stile dei messaggi
 * @author Andrea Del Corto-Simone Giacomelli 5IA-07
 */
public class MessageStyle {
    
/**
 * Metodo che inizializza lo stile il colore e la dimensione dei caratteri
 * @param color oggetto di tipo Color che definisce il colore del testo
 * @param fontFamily stringsa che conterrà il nome del font da usare
 * @param size variabile intera che definirà la grandezza del testo
 * @return 
 */
    public static AttributeSet styleMessageContent(Color color, String fontFamily, int size){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, fontFamily); //  FontFamily
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        aset = sc.addAttribute(aset, StyleConstants.FontSize, size);
        return aset;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;

import java.awt.event.MouseEvent;

import javax.swing.JTextArea;

/**
 *
 * @author AleX
 */
public class GUINodeListener implements GraphMouseListener {
        private JTextArea textArea;
    
    public GUINodeListener(JTextArea textArea){
        this.textArea=textArea;
    }
    
    @Override
    public void graphClicked(Object v, MouseEvent me) {
            Node n=(Node)v;
            this.textArea.append("NODO ID:"+n.getId()+"\n");
            this.textArea.append("######################################################################\n");
    }

    @Override
    public void graphPressed(Object v, MouseEvent me) {
    }

    @Override
    public void graphReleased(Object v, MouseEvent me) {
    }
    
}

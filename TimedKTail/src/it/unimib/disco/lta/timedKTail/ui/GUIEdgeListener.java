/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextArea;

/**
 *
 * @author AleX
 */
public class GUIEdgeListener implements ItemListener {
    private JTextArea textArea;
    private Transition ultima;
    
    public GUIEdgeListener(JTextArea textArea){
        this.textArea=textArea;
        this.ultima=null;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        String clauseRelative="";

        Transition t=(Transition)e.getItem();
        if( (ultima!=null) && (ultima.equals(t)) ){
            
        }else{
            this.textArea.append("ARCO ID:"+t.getId()+"\n");
            this.textArea.append(" Attivita': "+t.getActivity().getName()+"\n");
            this.textArea.append(" Tipo: "+t.getType()+"\n");
            this.textArea.append(" Guardia: \n");
            this.textArea.append("   Clausole AbsoluteClock: \n");
            this.textArea.append("       ");
            for(Clause c:t.getGuard().getClauses()){
                if(c.getClock() instanceof it.unimib.disco.lta.timedKTail.JTMTime.AbsoluteClock){
                    this.textArea.append(c.toString()+"  ");
                }
            }
            this.textArea.append("\n");
            this.textArea.append("   Clausole RelativeClock: \n");
            this.textArea.append("       ");
            for(Clause c:t.getGuard().getClauses()){
                if(c.getClock() instanceof it.unimib.disco.lta.timedKTail.JTMTime.RelativeClock){
                    clauseRelative=clauseRelative+c.toString()+"  ";
                }
            }
            if(clauseRelative.equals("")){
                this.textArea.append("CLAUSOLE NON PRESENTI");
            }else{
                this.textArea.append(clauseRelative);
            }
            this.textArea.append("\n");
            this.textArea.append("    Reset: "+t.getResets().toString()+"\n");  
            this.textArea.append("######################################################################\n");
            ultima=t;
        }
    }
    
}

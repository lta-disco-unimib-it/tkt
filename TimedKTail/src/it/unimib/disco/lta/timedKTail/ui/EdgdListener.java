/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 * @author AleX
 */
public class EdgdListener implements ItemListener{

    @Override
    public void itemStateChanged(ItemEvent e) {
        Transition t=(Transition)e.getItem();
        System.out.println("Edge ID:"+t.getId());
        System.out.println("    Activity: "+t.getActivity().getName());
        System.out.println("    Type: "+t.getType());
        System.out.println("    Guard: "+t.getGuard().toString());
        System.out.println("    Reset: "+t.getResets().toString());
        System.out.println("    From: "+t.getNodeFrom().getId());
        System.out.println("    To: "+t.getNodeTo().getId());
        
        
    }
    
}

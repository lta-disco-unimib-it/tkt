/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;

import java.awt.event.MouseEvent;

/**
 *
 * @author AleX
 */
public class NodeListener implements GraphMouseListener{

    @Override
    public void graphClicked(Object v, MouseEvent me) {
            Node n=(Node)v;
            System.out.println("Node ID: "+n.getId());
    }

    @Override
    public void graphPressed(Object v, MouseEvent me) {
    }

    @Override
    public void graphReleased(Object v, MouseEvent me) {
    }
    
}

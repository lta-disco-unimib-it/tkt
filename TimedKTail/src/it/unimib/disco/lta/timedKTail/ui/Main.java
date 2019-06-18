/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;


/**
 *
 * @author AleX
 */
import static it.unimib.disco.lta.timedKTail.traces.Statistica.getStatistica;
import it.unimib.disco.lta.timedKTail.JTMTime.*;
import it.unimib.disco.lta.timedKTail.algorithm.*;
import it.unimib.disco.lta.timedKTail.traces.ObserverTimedAutomataTraceBuilder;
import it.unimib.disco.lta.timedKTail.traces.ObserverValidateTrace;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.util.JavaRunner;
import it.unimib.disco.lta.timedKTail.validation.Validation;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





public class Main {


	public static final String TKT_INTERVAL_INFERENCE_POLICY = "tkt.intervalInferencePolicy";
	public static final String TKT_POLICY_VALIDATE_AFTER_MERGING = "tkt.policy.validateAfterMerging";
	public static final String TKT_POLICY_INCLUDE_NESTED_CALLS_TIME = "tkt.policy.includeNestedCallsTime";
	public static final String TKT_POLICY_DERIVE_GLOBAL_CLOCK = "tkt.policy.deriveGlobalClock";
	public static final String TKT_POLICY_NORMAL_DISTRIBUTION_CONFIDENCE = "tkt.policy.normalDistributionConfidence";
	public static final String TKT_POLICY_MIN_MAX_INCREASE_FACTOR = "tkt.policy.minMaxIncreaseFactor";
	public static final String TKT_POLICY_K_POLICY_ONE_VAL = "tkt.policy.kPolicyOneVal";
	public static final String TKT_POLICY_ONE = "tkt.policy.one";
	
	
	//*********POLITICHE PER UNA SINGOLA CLAUSA*********//
	// VALORE 1: POLITICA CONSERVATIVA
	// VALORE 2: POLITICA CONSERVATIVA CON PARAMETRO K -> kPoliticsOneVal
	// VALORE 3: POLITICA ELIMINAZIONE
	private static final int politicsOneVal=Integer.getInteger(TKT_POLICY_ONE, 3);
	private static final int kPoliticsOneVal=Integer.getInteger(TKT_POLICY_K_POLICY_ONE_VAL, 0);

	//*********POLITICHE PER UNA MULTY CLAUSA*********//
	// VALORE 1: POLITICA MAGGIORANZA
	// VALORE 2: POLITICA MINORANZA
	// VALORE 3: POLITICA INTERVALLO
	// VALORE 4: POLITICA INTERVALLO NORMALE
	
	private static final double deltaForRangeCalculation=Double.valueOf( System.getProperty(TKT_POLICY_MIN_MAX_INCREASE_FACTOR, "0.05" ) );
	private static final double normalDistributionConfidence=Double.valueOf( System.getProperty(TKT_POLICY_NORMAL_DISTRIBUTION_CONFIDENCE, "0.95"));
	private static boolean inferAbsoluteClocks = Boolean.valueOf( System.getProperty(TKT_POLICY_DERIVE_GLOBAL_CLOCK, "true") );
	private static boolean includeNestedCallsTime = Boolean.valueOf( System.getProperty(TKT_POLICY_INCLUDE_NESTED_CALLS_TIME, "true") );
	private static boolean validateAfterMerging = Boolean.valueOf( System.getProperty(TKT_POLICY_VALIDATE_AFTER_MERGING, "false") );
	
	//path di salvataggio automi

	private static final Logger logger = LogManager.getLogger(Main.class);
	private static FileWriter fw;
	private static boolean showOtherErrors = Boolean.valueOf( System.getProperty("showOtherErrors", "true") );
	
	
	private enum policyMultivalOptions { MinMax, Gamma } 
	
	public static void main(String[] args) throws IOException{		
		File destFolder = new File ( args[0] );

		String fTraces = args[1];

		String fTraceValidation = null;
		if ( args.length > 2 ){
			fTraceValidation = args[2];
		}
		
		
		
		TimedAutomata ta = inferTimedAutomata(fTraces);
		
		saveAutomata(ta,getDefaultAutomataPath(ta, destFolder));

		if ( fTraceValidation != null ){
			//validazione tracce
			validateTrace(ta, fTraceValidation,true,false,true);
			//drawGraph1(ta,"Stadio2"+"path: "+fTraces);
			//saveAutomata(ta, pathSaveTA);
			//drawGraph1(loadAutomata(),"caricatoFILE");
			drawGraph1(ta,"Stadio2"+"path: "+fTraces);
			//statistiche di validazione
			statistica(ta);

			//visualizzazione
			getStatistica();
		}
		//stampaRisultatiTrace(ta.getNodes().size(),ta.getTransitions().size()); // metodo per salvare file txt con statistiche
	}

	public static TimedAutomata inferTimedAutomata(String fTraces) {
		String policyMultivalString = System.getProperty(TKT_INTERVAL_INFERENCE_POLICY,policyMultivalOptions.MinMax.name());
		
		int policyMultyVal=-1;
		if ( policyMultivalString.equals(policyMultivalOptions.MinMax.name()) || policyMultivalString.equals("3") ){
			policyMultyVal=3;
		} else if ( policyMultivalString.equals(policyMultivalOptions.Gamma.name()) || policyMultivalString.equals("4") ){
			policyMultyVal=4;
		}


		TimedAutomata ta = inferAutomata(fTraces,politicsOneVal,kPoliticsOneVal,policyMultyVal,deltaForRangeCalculation,normalDistributionConfidence,inferAbsoluteClocks, includeNestedCallsTime, validateAfterMerging);
		return ta;
	}


	
	public static TimedAutomata inferAutomata(String fTraces, int politicsOneVal, double kPoliticsOneVal, int politicsMultyVal, double deltaForRangeCalculation, double normalDistributionConfidence, boolean inferAbsoluteClocks, boolean includeNestedCallsTime, boolean validateAfterMerging) {

		
		Integer k=2;
		TimedAutomata ta;
		Policy poli = new Policy(politicsOneVal,kPoliticsOneVal,politicsMultyVal,includeNestedCallsTime, validateAfterMerging);

		
		poli.setInferGuardsForAbsoluteClocks( inferAbsoluteClocks );
		poli.setDeltaForRangeCalculation(deltaForRangeCalculation);
		poli.setNormalDistributionConfidence(normalDistributionConfidence);
		
		boolean cachePendingCalls = Boolean.getBoolean("cachePendingCalls");
		if ( cachePendingCalls ){
			poli.setCachePendingCalls(cachePendingCalls);
		}
		
		boolean useIncrementalMerging = Boolean.getBoolean("useIncrementalMerging");
		if ( useIncrementalMerging ){
			poli.setUseIncrementalMerging(useIncrementalMerging);
		}
		
		TimedKTail kt = new TimedKTail(k,poli);
		//valore primo del costruttore è identificativo dell'osservatore 
		ObserverTimedAutomataTraceBuilder o = new ObserverTimedAutomataTraceBuilder(1,kt);
		Parser parser = new Parser(o);
		parser.readFolder(fTraces);

		kt.resolve();
		ta=kt.getTimedAutomata();
		return ta;
	}

	/*Metodo aggiuntivo, se si vuole in uscita in file TXT
     con salvate le percentuali di validazione,
     numero nodi, transizioni e la cusa di rifiuto traccia*/
	public static void stampaRisultatiTrace() throws IOException{ 
		String stringInd = "C:/path/prova1/";           // cartella dove voglio salvare il file txt       
		File filePath = new File(stringInd);                       
		File salva = new File("C:/path/file.txt");
		/*if (new File("c:/Users/hamza/Desktop/prova1/file.txt").isFile()){
            fw = new FileWriter(salva);
        }else{*/
		try {
			fw = new FileWriter(salva,true);     
		} catch (IOException e) {	
		}
		// }
		fw.write("\n" + Validation.getNumTracVal()+"/" +Validation.getNumTrace()+ " TRACCE VALIDATE" +
				" Guardie Violate " +Validation.getNumGuardieViolate()+
				" Eventi Violati "+Validation.getNumEventiViolati());
		fw.flush();

	}

	public static void statistica(TimedAutomata ta){
		long nTransition = 0;
		long nNode = 0;
		for(Transition t:ta.getTransitions()){
			nTransition++;
		}
		for(Node n:ta.getNodes()){
			nNode++;
		}
		System.out.println("Numero Transizioni: "+nTransition);
		System.out.println("Numero Nodi: "+nNode);
	}

	public static void validateTrace(TimedAutomata ta,String validationFolderPath, boolean validateAbsoluteClocks,
			boolean checkForGuardsNotReset, boolean checkGuards){
		
		
		boolean useMethodExcutionTime  = Boolean.parseBoolean(System.getProperty("useMethodExcutionTime", "false"));
		boolean traceErrorSequences  = Boolean.parseBoolean(System.getProperty("tkt.traceErrorSequences","false") );
		
		Validation validation = new Validation(ta,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards,useMethodExcutionTime,traceErrorSequences);
		ObserverValidateTrace oValidate = new ObserverValidateTrace(1,validation);
		validation.setRecordInvalidTraces(true);
		validation.setRecordErrorMessage(true);
		
		
		
		Parser parser2 = new Parser(oValidate);
		
		File f = new File ( validationFolderPath );
		if ( f.isFile() ){
			parser2.readFile(validationFolderPath);
		} else {
			parser2.readFolder(validationFolderPath);
		}
		
		List<Trace> valid = oValidate.getValidTraces();
		List<Trace> invalid = oValidate.getInvalidTraces();

		
		System.out.println("==== TRACES VALIDATION RESULTS ==== " );
		for ( Trace t : invalid ){
			System.out.println("Trace : "+t.getFilePath() );
			
			System.out.println("Main error: ");
			ValidationError e = oValidate.getError(t);
			printErrorDetails(t, e);
			
			if ( showOtherErrors ){
				System.out.println("Other errors: ");
				for ( ValidationError oe : oValidate.getErrors(t) ){
					printErrorDetails(t, oe);	
				}
			}
		}
		
		System.out.println("Valid traces ("+valid.size()+") :" );
		for ( Trace t : valid ){
			System.out.println(t);
		}
		
		System.out.println("Invalid traces ("+invalid.size()+") :" );
		for ( Trace t : invalid ){
			System.out.println(t+" "+oValidate.getError(t).getErrorType());
		}
	}

	public static void printErrorDetails(Trace t, ValidationError e) {
		System.out.println(t+" event#:"+e.getLine()+" "+e.getErrorType());
		System.out.println(e.getMsg());
		System.out.println("\n");
	}

	public static void drawGraph1(TimedAutomata ta,String name){
		
		

		Collection<Node> n = ta.getNodes();
		Collection<Transition> t = ta.getTransitions();
		SparseMultigraph<Node, Transition> graph = new SparseMultigraph();
		for(it.unimib.disco.lta.timedKTail.JTMTime.Node nodo:n){
			graph.addVertex(nodo);
		}

		for(it.unimib.disco.lta.timedKTail.JTMTime.Transition t1:t){
			graph.addEdge(t1,t1.getNodeFrom(), t1.getNodeTo(), EdgeType.DIRECTED);
		}
		drawGraph(graph,name);

	}
	private static void drawGraph(SparseMultigraph<Node, Transition> g,String name){
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Node, Transition> layout = new ISOMLayout(g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		VisualizationViewer<Node,Transition> vv =
				new VisualizationViewer<Node, Transition>(layout);
		vv.setPreferredSize(new Dimension(500,500)); //Sets the viewing area size

		//modifico colore dei nodi
		Transformer<Node,Paint> vertexPaint = new Transformer<Node,Paint>() {
			public Paint transform(Node n) {
				if(n.isInitialState()){
					return Color.RED;
				}
				if(n.isInitialState() && n.isFinalState() ){
					return Color.ORANGE;
				}
				if( n.isFinalState() ){
					return Color.YELLOW;
				}
				return Color.GREEN;
			}
		};
		//modifico colore degli archi
		Transformer<Transition,Paint> EdgePaint = new Transformer<Transition,Paint>() {
			public Paint transform(Transition t) {
				return Color.YELLOW;
			}
		};

		Transformer<Node,Shape> vertexSize = new Transformer<Node,Shape>(){
			public Shape transform(Node i){
				Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
				// in this case, the vertex is twice as large
				if(i.isInitialState()) return AffineTransform.getScaleInstance(1.2, 1.2).createTransformedShape(circle);
				else return circle;
			}
		};

		//Modifico Testo dei nodi
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller() {
			@Override
			public String transform(Object v) {
				Node n=(Node)v;
				return super.transform(n.getId());
			}});

		final Stroke edgeStroke = new BasicStroke(3.0f);
		Transformer<Transition, Stroke> edgeStrokeTransformer =
				new Transformer<Transition, Stroke>() {
			@Override
			public Stroke transform(Transition s) {
				return edgeStroke;
			}
		};

		vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		//         vv.getRenderContext().setEdgeFillPaintTransformer(EdgePaint);
		vv.getRenderContext().setEdgeDrawPaintTransformer(EdgePaint);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		//permette di utilizzare il mouse su interfaccia grafica
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		//permette di utilizzare il mouse su interfaccia grafica
		DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(graphMouse);


		vv.getPickedEdgeState().addItemListener(new EdgdListener());
		vv.addGraphMouseListener(new NodeListener());
		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setVisible(true); 
		frame.setLocation(300, 300);
	}

//	public static boolean saveAutomataUsingDefaultFileName(TimedAutomata ta, File pathSaveTA){
//
//		File outputFile = getDefaultAutomataPath(ta, pathSaveTA);
//		return saveAutomata(ta, outputFile);
//	}

	public static File getDefaultAutomataPath(TimedAutomata ta, File destFolder) {
		return new File ( destFolder, ta.getName()+".jtm" );
	}

	public static boolean saveAutomata(TimedAutomata ta, File outputFile) {
		logger.info("Saving automata"+ta.getName()+" to: "+outputFile.getAbsolutePath());
		try
		{	

			File parent = outputFile.getParentFile();
			
			if ( parent != null ){
				parent.mkdirs();
			}

			FileOutputStream fileOut = new FileOutputStream(outputFile,false);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(ta);
			out.close();
			fileOut.close();
			
			return true;
		}catch(IOException i){
			i.printStackTrace();
			return false;
		}
	}

	public static TimedAutomata loadAutomata(String path){
		TimedAutomata ta = null;
		try
		{

			File filePath = new File( path );
			File fileToOpen;

			if ( filePath.isDirectory() ){
				File[] files = filePath.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						// TODO Auto-generated method stub
						return pathname.getName().endsWith("jtm");
					}
				});
				
				
				fileToOpen = files[0];
			} else {
				fileToOpen = new File( path );
			}





			FileInputStream fileIn = new FileInputStream(fileToOpen);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ta = (TimedAutomata) in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i){
			i.printStackTrace();
			return null;
		}catch(ClassNotFoundException c){
			System.out.println("Employee class not found");
			c.printStackTrace();
			return null;
		}
		return ta;
	}

	public static void saveAutomata(TimedAutomata ta, String incrementalAutomata) {
		File dest = new File( incrementalAutomata+".jtm" );
		saveAutomata(ta, dest);
	}


}

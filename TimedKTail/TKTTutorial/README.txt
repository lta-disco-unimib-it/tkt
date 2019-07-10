This file contains instructions to execute TKT.

The TKT tool provides two main functionalities: 
1) infer a timed automaton from a set of valid traces
2) check one or more traces against an inferred model to identify anomalies.

Following instructions are expected to be executed inside folder "Example1"

0) Copy the tkt.jar file inside the current folder or create a symbolic link.

1) Infer a timed automata model

Execute the following command from console:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.InferModel TA.jtml validTraces/


1.2) Infer a timed automata model that do not include global clocks

 
Execute the following command from console:
java -Dtkt.policy.inferAbsoluteClocks=false -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.InferModel TA.jtml validTraces/




1.3) Other options that control model inference


	-Dtkt.policy.deriveGlobalClock=<value> (true/false)
	
	-Dtkt.intervalInferencePolicy=<value> (could be MinMax or Gamma, default is MinMax)
	
	-Dtkt.policy.minMaxIncreaseFactor=<value> ( <value> could be any number, usually in the range 0.0 - 1.0 )  (has effect on guards generation only if MinMax policy is adopted)
	
	-Dtkt.policy.normalDistributionConfidence=<value> ( <value> could be any number in the range 0.0 - 1.0, usually 0.95 or 0.99 )  
	(has effect on guards generation only if Gamma policy is adopted)


1.4) Visualize the inferred automaton

Execute the following command from console:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.VisualizeAutomata TA.jtml

2) Check traces 

2.1) Case 1: all the traces in a folder are valid. This happens for example if we consider the traces used to infer the model.

Execute the folowing command:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml validTraces/


The command produces the following output
	==== TRACES VALIDATION RESULTS ==== 
	Valid traces (3) :
	[Trace path:validTraces/trace.csv traceN:1]
	[Trace path:validTraces/trace2.csv traceN:1]
	[Trace path:validTraces/trace3.csv traceN:1]
	Invalid traces (0) :




2.2a) Case 2a: an event timestamp violates a guard condition  
In this example we compare a single trace against the model. The following example shows that TKTAIL is able to detect 
that event "read1.0" did not terminate on time, since the guard condition 61<= T <= 68 has been violated (actual time is 145)
The name "T" indicates a the absolute clock.

Execute the following command:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml invalidTraces/trace.csv.invalidGuard.1

The following output is generated:
	==== TRACES VALIDATION RESULTS ==== 
	Trace : invalidTraces/trace.csv.invalidGuard.1
	Main error: 
	[Trace path:invalidTraces/trace.csv.invalidGuard.1 traceN:1] event#:8 VIOLATED_GUARD
	Violated guard 
	Violating event: 
	   Activity: read1.0 
	   Type: E 
	   Time: 145 
	Transition info: 
	   Activity: read1.0 
	   Type: E 
	   Violated Clause: 
	61<= T <= 68
	Processed events: 
	    read1.0 B     (0 -> 1) 
	    read2.0 B     (1 -> 2) 
	    read3.0 B     (2 -> 3) 
	    read4.0 B     (3 -> 4) 
	    read4.0 E     (4 -> 13) 
	    read3.0 E     (13 -> 14) 
	    read2.0 E     (14 -> 23) 
	Total events: 7
	
	
	Other errors:



When reporting a violation the tool indicates details about the type of violation observed, 
and the subtrace matched by the automaton.
A model can be violated because of different reasons: 
	1) the trace is fully accepted but the last state reached is not final, 
	2) an event does not match a transition on the automaton, 
	3) an event is accepted but the timing associated with the event does not match the guard condition. 
Since different prefix of a same trace can be accepted by different paths of the automaton, 
the tool reports the model violation observed after the longer sequence of events is accepted.
The output that follows "Other errors" indicates instead other violations identified on the paths that 
accept shorter subtraces.
	


2.2b) Case 2b: verification without absolute clocks

The following example set the option validateAbsoluteClocks to false, in order to ignore global clocks during check.
The violation is still detected because read1.0 takes more than the usual [61;68] milliseconds to execute.
Names that follow the pattern "C*" indicate relative clocks.

Execute the following command:
	java -DvalidateAbsoluteClocks=false -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml invalidTraces/trace.csv.invalidGuard.1


Output:
	==== TRACES VALIDATION RESULTS ====
	Trace : invalidTraces/trace.csv.invalidGuard.1
	Main error:
	[Trace path:invalidTraces/trace.csv.invalidGuard.1 traceN:1] event#:8 VIOLATED_GUARD
	Violated guard
	Violating event:
	   Activity: read1.0
	   Type: E
	   Time: 145
	Transition info:
	   Activity: read1.0
	   Type: E
	   Violated Clause:
	61<= CK9 <= 68
	Eventi processati:
	    read1.0 B     (0 -> 1)
	    read2.0 B     (1 -> 2)
	    read3.0 B     (2 -> 3)
	    read4.0 B     (3 -> 4)
	    read4.0 E     (4 -> 13)
	    read3.0 E     (13 -> 14)
	    read2.0 E     (14 -> 23)
	Total events: 7





2.2c) Case 2c: ignore guards

We may simply check if the sequence of events appearing in a trace is accepted by the model.

If we ignore guards, the trace used in the examples above is accepted.

Execute teh following command:
	java -DcheckGuards=false -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml invalidTraces/trace.csv.invalidGuard.1


==== TRACES VALIDATION RESULTS ====
Valid traces (1) :
[Trace path:invalidTraces/trace.csv.invalidGuard.1 traceN:1]


 

 




2.3) Case 3: an unexpected event is observed

Execute:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml invalidTraces/trace.csv.invalidEvent.1 


Output:
	==== TRACES VALIDATION RESULTS ==== 
	Trace : invalidTraces/trace.csv.invalidEvent.1
	Main error: 
	[Trace path:invalidTraces/trace.csv.invalidEvent.1 traceN:1] event#:4 UNMATCHED_EVENT
	Invalid event 
	Event details: 
	   Activity: read3.0 
	   Type: B 
	Processed events: 
	    read1.0 B     (0 -> 1) 
	    read2.0 B     (1 -> 2) 
	    read3.0 B     (2 -> 3) 
	    read4.0 B     (3 -> 4) 
	Total events: 4


In this example event read3.0 is not expected to occur after read4.0.




2.4) Case 4: termination in a non-final state

 

Execute the following command:
	java -cp tkt.jar it.unimib.disco.lta.timedKTail.ui.ValidateTraces TA.jtml invalidTraces/trace.csv.earlyTermination.1

 
Output:
	==== TRACES VALIDATION RESULTS ====
	
	Trace : invalidTraces/trace.csv.earlyTermination.1
	
	Main error:
	
	[Trace path:invalidTraces/trace.csv.earlyTermination.1 traceN:1] event#:5 NOT_FINAL
	
	Trace terminates in a non-final state
	
	Event details:
	
	   Activity: read4.0
	
	   Type: E
	
	Matching trace:
	
	    read1.0 B     (0 -> 1)
	
	    read2.0 B     (1 -> 2)
	
	    read3.0 B     (2 -> 3)
	
	    read4.0 B     (3 -> 4)
	
	    read4.0 E     (4 -> 13)
	
	Total events: 5
	
	 

 


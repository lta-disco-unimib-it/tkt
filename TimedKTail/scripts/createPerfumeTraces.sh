politicsOneVal=3;
kPoliticsOneVal=2;
politicsMultyVal=3;
deltaForRangeCalculation=0.0;
normalDistributionConfidence=0.95;

CP=/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/bin:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Apache.Commons/commons-io-2.4.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Apache.Commons/commons-math3-3.6.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-algo-1.3-javadoc.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-algo-1.3-sources.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-algo-1.3.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-core-1.3-javadoc.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-core-1.3-sources.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-core-1.3.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-ui-1.3-javadoc.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-ui-1.3-sources.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/gs/gs-ui-1.3.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/collections-generic-4.01.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/jung-algorithms-2.0.1.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/jung-api-2.0.1.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/jung-graph-impl-2.0.1.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/jung-samples-2.0.1.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/Jung/jung-visualization-2.0.1.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/log4j/log4j-1.2-api-2.2.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/log4j/log4j-api-2.2-javadoc.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/log4j/log4j-api-2.2.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TimedKTail/lib/log4j/log4j-core-2.2.jar:/Users/fabrizio/LTA-GIT/LEARN-ModelGeneration/software/TimedKTail/TktTraceToPerfTrace/bin

PARENT=`pwd`

for x in 1 2 3 4 5 6 7 8 9 10
do
for PRJ in LZW_T10	LZW_T30		LZWdecom_T100	Merge_T30	RabinKarp_T100	merge_T100 LZW_T100	LZWdecom_T10	LZWdecom_T30	RabinKarp_T10	RabinKarp_T30	merge_T10
do
java -cp $CP  tkttracetoperftrace.TktTraceToPerfTrace $PRJ/$x/trace
java -cp $CP  tkttracetoperftrace.TktTraceToPerfTrace $PRJ/$x/traceValidation
done
done

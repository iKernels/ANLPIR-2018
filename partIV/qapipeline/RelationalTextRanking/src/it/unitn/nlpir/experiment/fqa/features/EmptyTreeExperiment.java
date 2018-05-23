package it.unitn.nlpir.experiment.fqa.features;

import java.util.Properties;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMVector;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), pruning ray=2
* @author IKernels group
 *
 */
public class EmptyTreeExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(EmptyTreeExperiment.class);
	public EmptyTreeExperiment(String configFile) {
		super(configFile);
	}
	
	public EmptyTreeExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public EmptyTreeExperiment(Properties p) {
		super(p);
	}
	
	public EmptyTreeExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		this.projector = Projectors.getFocusProjector(new PosChunkTreeBuilder(), pruningRay,  new StartsWithOrContainsTagPruningRule(), 
				new TreeLeafFinalizer(), doFocusMatch, typeFocusMatch, markFocusInQuestion);
	}

	@Override
	public Candidate generateCandidate(JCas questionCas, JCas documentCas, Result result) {
		Pair<String, String> qaProj = new Pair<String,String>("(ROOT)","(ROOT)");
		
		
		SVMVector featureVector = fb.getFeatures(new QAPair(questionCas, documentCas, result,
				new SVMVector(), qaProj));
		
		
		logger.debug((qaProj.getA()+"\t"+qaProj.getB()).replace("(", "[").replace(")", "]"));
		return new Candidate(result, qaProj, featureVector);
	}


}
package it.unitn.nlpir.features.providers.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.Pair;

public class NERProvider implements CollectionProvider {

	public Collection<String> getNERCollectionFromCas(JCas cas) {
		List<String> luList = new ArrayList<String>();
		

		for (NER ne : new ArrayList<>(JCasUtil.select(cas, NER.class))) {
			
			StringBuffer lemmas = new StringBuffer();
			boolean first = true;
			for (Token t :JCasUtil.selectCovered(cas, Token.class, ne)) {
				
				if (first) {
					first = false;
				}
				else {
					lemmas.append("_");
				}
				lemmas.append(t.getLemma().toLowerCase());
			}
			luList.add(lemmas.toString());	
			
		}
		
		return luList;
	}
	
	@Override
	public Pair<Collection<String>, Collection<String>> getCollections(QAPair qaPair) {
		// TODO Auto-generated method stub
		Collection<String> q = getNERCollectionFromCas(qaPair.getQuestionCas());
		Collection<String> d = getNERCollectionFromCas(qaPair.getDocumentCas());
		return new Pair<Collection<String>, Collection<String>>(q,d);
	}

}

package it.unitn.nlpir.util.wiki;


import java.util.List;
import java.util.Properties;

public interface WikipediaAnnotator {
	public List<WikipediaAnnotation> annotate(String text);
	public void setParams(Properties p);
}

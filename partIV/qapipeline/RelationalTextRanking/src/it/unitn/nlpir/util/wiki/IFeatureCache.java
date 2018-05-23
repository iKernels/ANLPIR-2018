package it.unitn.nlpir.util.wiki;

import java.io.IOException;
import java.util.List;



/**
 * Stores information about features extracted for a specific Wikipedia link
 * @author Kateryna
 *
 */
public interface IFeatureCache {
	public void add(String link, List<String> feats);
	public List<String> get(String link);
	public int getNewFeaturePoolSize();
	public void flush() throws IOException;
	public void close() throws IOException;
}

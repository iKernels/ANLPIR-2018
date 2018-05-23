package it.unitn.nlpir.util.wiki;

import java.io.IOException;
import java.util.List;

public interface IAnnotationsCache {

	public void initialize() throws Exception;
	public boolean containsAnnotation(String qid, String aid);
	public void addQuestionAnswerAnnotation(String qid, String aid, List<WikipediaAnnotation> questionAnnotation, List<WikipediaAnnotation> answerAnnotation);
	public void flush() throws IOException;
	public List<WikipediaAnnotation> getQuestionAnnotation(String qid, String aid);
	public List<WikipediaAnnotation> getAnswerAnnotation(String qid, String aid);
	public void close();
	
	
}

package it.unitn.nlpir.util.wiki.annotator;

import it.unitn.nlpir.util.wiki.WikipediaAnnotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Wikipedia annotation wrapper class to be used outside UIMA
 * @author Kateryna
 *
 */
public class SimpleAnnotation implements WikipediaAnnotation {
	private int start;
	private int end;
	private String link;
	private double confidence;
	public static Pattern p = Pattern.compile("\\[([0-9]+);([0-9]+);([^;]+);(.+)\\]");
	
	public String toString(){
		return "["+start+";"+end+";"+confidence+";"+link+"]";
	}
	
	public SimpleAnnotation(String string){
		Matcher m = p.matcher(string);
		if (m.find()){
			this.start = Integer.parseInt(m.group(1));
			this.end = Integer.parseInt(m.group(2));
			this.confidence = Double.parseDouble(m.group(3));
			this.link = m.group(4);
		}
	}
	
	/**
	 * 
	 * @param link
	 * @param start
	 * @param end
	 * @param confidence
	 */
	public SimpleAnnotation(String link, int start, int end, 
			double confidence) {
		super();
		this.start = start;
		this.end = end;
		this.link = link;
		this.confidence = confidence;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	@Override
	public int getStart() {
		// TODO Auto-generated method stub
		return start;
	}

	@Override
	public int getEnd() {
		// TODO Auto-generated method stub
		return end;
	}

	@Override
	public String getLink() {
		// TODO Auto-generated method stub
		return link;
	}

	@Override
	public double getConfidence() {
		// TODO Auto-generated method stub
		return confidence;
	}
	
}
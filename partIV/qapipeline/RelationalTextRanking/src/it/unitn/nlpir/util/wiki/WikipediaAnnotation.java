package it.unitn.nlpir.util.wiki;

public interface WikipediaAnnotation {
	public int getStart();
	public void setStart(int start);
	public int getEnd();
	public void setEnd(int end);
	public String getLink();
	public void setLink(String link);
	public double getConfidence();
	//public void readFromString(String string);
}

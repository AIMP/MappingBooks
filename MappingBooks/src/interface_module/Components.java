package interface_module;

import java.util.List;

public class Components {
	private List<Segment> segments;
	private List<Words> words;

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> l1) {
		this.segments = l1;
	}

	public List<Words> getWords() {
		return words;
	}

	public void setWords(List<Words> words) {
		this.words = words;
	}
}

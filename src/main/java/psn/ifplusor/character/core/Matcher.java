package psn.ifplusor.character.core;

import java.util.List;

public interface Matcher<E> {

	public void addKeyword(String key, E value, int level);
	
	public void addKeyword(List<String> lstKeys, E value, int level);
	
	public List<MatchedContent<E>> match(String content, Boolean intellWeight, boolean returnAll);
}

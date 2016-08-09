package psn.ifplusor.character.core;

public class MatchedContent<E> {
	
	E value = null;
	String key = ""; // 匹配关键字
	String content = ""; // 实际匹配串
	String context = ""; // 上下文
	int index = 0; // 下标
	int size = 0;
	int len = 0;
	int weight = 0; // 权重
	
	public MatchedContent() {
		
	}
	
	public MatchedContent(MatchedContent<E> mc) {

		this.value = mc.value;
		this.key = mc.key;
		this.content = mc.content;
		this.context = mc.context;
		this.index = mc.index;
		this.size = mc.size;
		this.len = mc.len;
		this.weight = mc.weight;
	}

	public E getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public String getContent() {
		return content;
	}

	public String getContext() {
		return context;
	}

	public int getIndex() {
		return index;
	}

	public int getSize() {
		return size;
	}

	public int getLen() {
		return len;
	}

	public int getWeight() {
		return weight;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		
		if (o == null)
			return false;
		
		MatchedContent<E> mc = (MatchedContent<E>) o;
		
		if (index == mc.index && value.equals(mc.value))
			return true;
		
		return false;
	}
}

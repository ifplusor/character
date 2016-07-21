package psn.ifplusor.character.dict;



public class DictEdge {
	
	private long value;
	
	private DictNode inNode;  // 左字
	private DictNode outNode; // 右字

	public DictEdge(DictNode inNode, DictNode outNode) {
		this.value = 0;
		this.inNode = inNode;
		this.outNode = outNode;
	}
	
	public synchronized void increase() {
		outNode.increaseCount(); // 增加右字出现次数
		if (inNode.getCharacter() != '^')
			outNode.increaseIn(); // 增加右字的入度
		if (outNode.getCharacter() != '$')
			inNode.increaseOut(); // 增加左字的出度
		value++;
	}

	public long getValue() {
		return value;
	}

	public DictNode getInNode() {
		return inNode;
	}

	public DictNode getOutNode() {
		return outNode;
	}
	
	@Override
	public String toString() {
		return inNode.getCharacter() + ":" + outNode.getCharacter() + ":" + value;
	}
}

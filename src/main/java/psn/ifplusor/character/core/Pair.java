package psn.ifplusor.character.core;

public class Pair<K, V> {
	K key;
	V value;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		
		if (o == null)
			return false;
		
		Pair<K, V> kv = (Pair<K, V>) o;
		
		if (key.equals(kv.key) && value.equals(kv.value))
			return true;
		
		return false;
	}
}

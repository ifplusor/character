package psn.ifplusor.character.core;

import java.util.ArrayList;
import java.util.List;

public class DistanceUtil {
	
	public static void main(String[] args) {
		List<Integer> lstA = new ArrayList<Integer>();
		List<Integer> lstB = new ArrayList<Integer>();
		
		lstA.add(1);
		//lstA.add(3);
		//lstA.add(9);
		//lstA.add(15);
		//lstA.add(20);
		//lstA.add(23);
		
		//lstB.add(4);
		//lstB.add(6);
		//lstB.add(7);
		//lstB.add(10);
		//lstB.add(12);
		lstB.add(19);
		
		System.out.println(minimalDistance(lstA, lstB));
	}
	
	/*
	 * 双边交替扫描，求最近两点距离
	 */
	public static int minimalDistance(List<Integer> lstA, List<Integer> lstB) {
		
		int lenA = lstA.size(), lenB = lstB.size();
		if (lenA * lenB == 0)
			return -1;
		
		int minimal = 100;
		int ia = 0, ib = 0;
		
		boolean flag = true;
		while (true) {
			int flip = 0;
			int min = Math.abs(lstA.get(ia) - lstB.get(ib));
			if (flag) {
				ib++;
			} else {
				ia++;
			}
			
			while (ia < lenA && ib < lenB) {
				int len = Math.abs(lstA.get(ia) - lstB.get(ib));
				if (flag) {  // 定 a, 动 b
					if (len <= min) {
						flip = 0;
						min = len;
						ib++;
					} else {
						ib--;
						if (++flip >= 2)  // 连续翻转两次
							break;
						flag = !flag;
						ia++;
					}
				} else {  // 定 b, 动 a
					if (len <= min) {
						flip = 0;
						min = len;
						ia++;
					} else {
						ia--;
						if (++flip >= 2)  // 连续翻转两次
							break;
						flag = !flag;
						ib++;
					}
				}
			}
			
			if (min < minimal) // 用局部最小值，更新全局最小值
				minimal = min;
			
			if (flip == 2) { // 重新开始
				ia++;
				ib++;
			} else { // 出界
				if (flag) { // 恢复临界状态，并移动另一边
					ib--;
				} else {
					ia--;
				}
				flag = !flag; // 翻转
				break;
			}
		}
		
		// 处理单边尾部不需要翻转
		if (flag) {
			int min = Math.abs(lstA.get(ia) - lstB.get(ib++));
			while (ib < lenB) {
				int len = Math.abs(lstA.get(ia) - lstB.get(ib));
				if (len < min) {
					min = len;
					ib++;
				} else {
					break;
				}
			}
			if (min < minimal) // 用局部最小值，更新全局最小值
				minimal = min;
		} else {
			int min = Math.abs(lstA.get(ia++) - lstB.get(ib));
			while (ia < lenA) {
				int len = Math.abs(lstA.get(ia) - lstB.get(ib));
				if (len < min) {
					min = len;
					ia++;
				} else {
					break;
				}
			}
			if (min < minimal) // 用局部最小值，更新全局最小值
				minimal = min;
		}
		
		return minimal;
	}

}

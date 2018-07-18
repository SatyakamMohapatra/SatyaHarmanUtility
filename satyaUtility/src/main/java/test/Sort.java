package test;

import java.util.Iterator;

public class Sort {
	public static void main(String[] args) {
		int a[] = {1,6,2,8,3,9,4,5};

		for (int j= 0;  j< a.length; j++) {
			for (int i = 0; i < a.length-j-1; i++) {
				if(a[i]>a[i+1]){
					int temp = a[i];
					a[i] = a[i+1];
					a[i+1] = temp;
				}
			}
		}
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}

package test;

import java.util.ArrayList;

public class Tset {
	ArrayList<Integer> list = new ArrayList<Integer>();
	
	Tset(ArrayList<Integer> list){
		System.out.println("test");
	}
	
	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		//list.add(args[0]);
	}

}

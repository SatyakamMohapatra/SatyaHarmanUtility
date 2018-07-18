package test;

public class MainDemoClass {
	public static void main(String[] args) {
		//int[] list = {5,2,6,3,1};
		//sortList(list);
		BinarySearchTree binarySearchTree = new BinarySearchTree();
		binarySearchTree.insert(5);
		binarySearchTree.insert(2);
		binarySearchTree.insert(6);
		binarySearchTree.search();
		System.out.println(binarySearchTree.contains(2));
		System.out.println(binarySearchTree.contains(9));
	}
	public static void sortList(int[] list){
		int max = 0;
		int var = 0;
		for(int i = 0;i<list.length;i++){
			//System.out.println("i = "+i);
			for(int j = 0;j<list.length-i;j++){
				//System.out.println("i = "+list[i]+" j = "+list[j]);
				if(list[i]>list[j]){
					max = list[i];
					var = i;
				}else{
					max = list[j];
					var = j;
				}
			}	
			System.out.println("max = "+max+" var = "+var);
			for(int k : list){
				System.out.print(k+",");
			}
			System.out.println(" ");
		}	
	}
}

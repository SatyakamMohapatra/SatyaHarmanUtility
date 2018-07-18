package test;

public class AlgoTest {
	private static int[] A;
 
	public static void main(String[] args) {
		
		int[] A = {9,7,5,3,1,2,4,6};
		int startIndx = 0;
		int lastIndx = (A.length - 1);
		mergeSort(A,startIndx,lastIndx);
		System.out.println(A);
	}

	private static void mergeSort(int[] A,int startIndx,int lastIndx) {
		if(startIndx < lastIndx){
			int midIndx = (startIndx + lastIndx)/2;
			mergeSort(A, startIndx, midIndx);
			mergeSort(A, midIndx+1, lastIndx);
			merge(A,midIndx,startIndx,lastIndx);
		}
	}

	private static void merge(int[] A,int midIndx,int startIndx,int lastIndx) {
		
		int originalArrayIndx = startIndx;
		int rightArrayIndx = 0;
		int leftArrayIndx = 0;
		int rightArray = midIndx-startIndx+1;
		int leftArray = lastIndx-midIndx;
		int[] right = new int[rightArray];
		int[] left = new int[leftArray];
		
		for (int i = 0; i < rightArray; i++){
			right[i] = A[startIndx+i];
		}
		for (int j = 0; j < leftArray; j++){
			left[j] = A[midIndx+1+j];
		}
		
		while(rightArrayIndx <= right.length-1 && leftArrayIndx <= left.length-1){
			if(right[rightArrayIndx] >= left[leftArrayIndx]){
				A[originalArrayIndx] = left[leftArrayIndx];
				leftArrayIndx++;
			}else{
				A[originalArrayIndx] = right[rightArrayIndx];
				rightArrayIndx++;
			}
			originalArrayIndx++;
		}
		
		while(rightArrayIndx<rightArray){
			A[originalArrayIndx] = right[rightArrayIndx];
			rightArrayIndx++;
			originalArrayIndx++;
		}
		
		while(leftArrayIndx<leftArray){
			A[originalArrayIndx] = left[leftArrayIndx];
			leftArrayIndx++;
			originalArrayIndx++;
		}
	}
	
}

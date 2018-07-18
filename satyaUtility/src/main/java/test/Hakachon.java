package test;

public class Hakachon {
	public static void main(String[] args) {
		System.out.println(validateRow("C A P G A F J L D H K D M J","P G K D M J C A A F J L D H"));
	}
	public static String validateRow(String input1,String input2)
	{
		input1 = input1.trim().replaceAll(" ", "");
		input2 =  input2.trim().replaceAll(" ", "");
		char[] givenValue = input1.toCharArray();
		char[] toCheck = input2.toCharArray();
		if(givenValue.length != toCheck.length)return "No";
		int startPoint = input2.indexOf(givenValue[0]);
		
		for(int i=0;i<givenValue.length;i++){
			///System.out.print("givenValue : "+givenValue[i] +" toCheck : "+ toCheck[startPoint]);
			///.out.println();
			if(givenValue[i]!=toCheck[startPoint]){
				return "No";
			}else{
				startPoint++;
			}
			if(startPoint==toCheck.length) {
				startPoint=0;
			}
		}
		return "yes";
	}
}

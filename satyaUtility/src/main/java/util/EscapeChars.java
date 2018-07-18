package util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public final class EscapeChars {

	public static String getEscapedString(String value){
	
		String escapedStr="";
		if(value != null){
			for(int k=0;k<value.length();k++)
			{
				char ch=value.charAt(k);
				int ival=ch;
				if(ival==46 || ival==32 || (ival>=48 && ival<=57) || (ival>=65 && ival<=90) || (ival>=97 && ival<=122))
				{
					escapedStr = escapedStr+ch;
				}else
				{
					escapedStr = escapedStr+"&#"+ival+";";
				}
			}
		}		
		return escapedStr;
	}	
   public static String escapeForURL(String text){
	   if(text == null){
		   return "";
	   }
	   String value = getEscapedString(text);
     final StringBuilder result = new StringBuilder();
     final StringCharacterIterator iterator = new StringCharacterIterator(value);
     char character =  iterator.current();
     while (character != CharacterIterator.DONE ){
       if (character == '<') {
         result.append("%3C");
       }
       else if (character == '>') {
         result.append("%3E");
       }
       else if (character == '&') {
         result.append("%26");
       }
       else if (character == '#') {
           result.append("%23");
         }
       else if (character == '%') {
           result.append("%25");
         }
       else if (character == '{') {
           result.append("%7B");
         }
       else if (character == '}') {
           result.append("%7D");
         }
       else if (character == '\\') {
           result.append("%5C");
         }
       else if (character == '^') {
           result.append("%5E");
         }
       else if (character == '~') {
           result.append("%7E");
         }
       else if (character == '[') {
           result.append("%5B");
         }
       else if (character == '\'') {
           result.append("\\'");
         }
       else if (character == ']') {
           result.append("%5D");
         }
       else if (character == '`') {
           result.append("%60");
         }
       else if (character == '\'') {
           result.append("\\'");
         }
       else if (character == ';') {
           result.append("%3B");
         }
       else if (character == '/') {
           result.append("%2F");
         }
       else if (character == '?') {
           result.append("%3F");
         }
       else if (character == ':') {
           result.append("%3A");
         }
       else if (character == '@') {
           result.append("%40");
         }
       else if (character == '=') {
           result.append(" %3D");
         }
       else if (character == '$') {
           result.append("%24");
         }
       else if (character == '|') {
           result.append("%7C");
         }
       else {
          result.append(character);
       	}
       character = iterator.next();
     }
     return result.toString();
  }
	/**
   Escape characters for text appearing in HTML markup.
   
   <P> This is done by replacing the control
   characters with their escaped equivalents.  
   
   
   <P>The following characters are replaced with corresponding 
   HTML character entities :
   <table border='1' cellpadding='3' cellspacing='0'>
   <tr><th> Character </th><th>Replacement</th></tr>
   <tr><td> < </td><td> &lt; </td></tr>
   <tr><td> > </td><td> &gt; </td></tr>
   <tr><td> & </td><td> &amp; </td></tr>
   <tr><td> " </td><td> &quot;</td></tr>
   <tr><td> \t </td><td> &#009;</td></tr>
   <tr><td> ! </td><td> &#033;</td></tr>
   <tr><td> # </td><td> &#035;</td></tr>
   <tr><td> $ </td><td> &#036;</td></tr>
   <tr><td> % </td><td> &#037;</td></tr>
   <tr><td> ' </td><td> &#039;</td></tr>
   <tr><td> ( </td><td> &#040;</td></tr> 
   <tr><td> ) </td><td> &#041;</td></tr>
   <tr><td> * </td><td> &#042;</td></tr>
   <tr><td> + </td><td> &#043; </td></tr>
   <tr><td> , </td><td> &#044; </td></tr>
   <tr><td> - </td><td> &#045; </td></tr>
   <tr><td> . </td><td> &#046; </td></tr>
   <tr><td> / </td><td> &#047; </td></tr>
   <tr><td> : </td><td> &#058;</td></tr>
   <tr><td> ; </td><td> &#059;</td></tr>
   <tr><td> = </td><td> &#061;</td></tr>
   <tr><td> ? </td><td> &#063;</td></tr>
   <tr><td> @ </td><td> &#064;</td></tr>
   <tr><td> [ </td><td> &#091;</td></tr>
   <tr><td> \ </td><td> &#092;</td></tr>
   <tr><td> ] </td><td> &#093;</td></tr>
   <tr><td> ^ </td><td> &#094;</td></tr>
   <tr><td> _ </td><td> &#095;</td></tr>
   <tr><td> ` </td><td> &#096;</td></tr>
   <tr><td> { </td><td> &#123;</td></tr>
   <tr><td> | </td><td> &#124;</td></tr>
   <tr><td> } </td><td> &#125;</td></tr>
   <tr><td> ~ </td><td> &#126;</td></tr>
   </table>
   
  */
   public static String forHTML(String text){
	   if(text == null){
		   return "";
	   }
	     final StringBuilder result = new StringBuilder();
	     final StringCharacterIterator iterator = new StringCharacterIterator(text);
	     char character =  iterator.current();
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }
	       else if (character == '>') {
	         result.append("&gt;");
	       }
	       else if (character == '&') {
	         result.append("&amp;");
	      }
	       else if (character == '\"') {
	         result.append("&quot;");
	       }
	       else if (character == '\t') {
	         addCharEntity(9, result);
	       }
	       else if (character == '!') {
	         addCharEntity(33, result);
	       }
	       else if (character == '#') {
	         addCharEntity(35, result);
	       }
	       else if (character == '$') {
	         addCharEntity(36, result);
	       }
	       else if (character == '%') {
	         addCharEntity(37, result);
	       }
	       else if (character == '\'') {
	         addCharEntity(39, result);
	       }
	       else if (character == '(') {
	         addCharEntity(40, result);
	       }
	       else if (character == ')') {
	         addCharEntity(41, result);
	       }
	       else if (character == '*') {
	         addCharEntity(42, result);
	       }
	       else if (character == '+') {
	         addCharEntity(43, result);
	       }
	       else if (character == ',') {
	         addCharEntity(44, result);
	       }
	       else if (character == '-') {
	         addCharEntity(45, result);
	       }
	       else if (character == '.') {
	         addCharEntity(46, result);
	       }
	       else if (character == '/') {
	         addCharEntity(47, result);
	       }
	       else if (character == ':') {
	         addCharEntity(58, result);
	       }
	       else if (character == ';') {
	         addCharEntity(59, result);
	       }
	       else if (character == '=') {
	         addCharEntity(61, result);
	       }
	       else if (character == '?') {
	         addCharEntity(63, result);
	       }
	       else if (character == '@') {
	         addCharEntity(64, result);
	       }
	       else if (character == '[') {
	         addCharEntity(91, result);
	       }
	       else if (character == '\\') {
	         addCharEntity(92, result);
	       }
	       else if (character == ']') {
	         addCharEntity(93, result);
	       }
	       else if (character == '^') {
	         addCharEntity(94, result);
	       }
	       else if (character == '_') {
	         addCharEntity(95, result);
	       }
	       else if (character == '`') {
	         addCharEntity(96, result);
	       }
	       else if (character == '{') {
	         addCharEntity(123, result);
	       }
	       else if (character == '|') {
	         addCharEntity(124, result);
	       }
	       else if (character == '}') {
	         addCharEntity(125, result);
	       }
	       else if (character == '~') {
	         addCharEntity(126, result);
	       }
	       else {
		 		int ival = (int)character;
				if(isCharToBeEscaped(ival,false)) 
				{
					result.append("&#"+ival+";");
				}else
				{
					result.append(character);
				}
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	  }
 
	public static boolean isCharToBeEscaped(int charCode,boolean doEscapeDot)
	{
		//should escape only the required once - special characters only
		boolean isCharToBeEscaped = doEscapeDot ? (charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122)) : (charCode==46 || charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122));
		if(isCharToBeEscaped) 
		{
			return false;
		}
		return true;
	}
   private static void addCharEntity(Integer aIdx, StringBuilder aBuilder){
	    String padding = "";
	    if( aIdx <= 9 ){
	       padding = "00";
	    }
	    else if( aIdx <= 99 ){
	      padding = "0";
	    }
	    else {
	      //no prefix
	    }
	    String number = padding + aIdx.toString();
	    aBuilder.append("&#" + number + ";");
	  }

}

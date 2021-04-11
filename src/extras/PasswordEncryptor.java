package extras;

import java.math.BigInteger;

public class PasswordEncryptor {

	public final static BigInteger HASHMAX = new BigInteger("2").pow(512).subtract(new BigInteger("569"));
	
	
	public static String getHashedMessage(String message) {
		
		BigInteger first = String_To_Value(message);
		
		message += "gohappyduckling";
		message = shuffle(message);
		message += "football";
		message = shuffle(message);
		
		for (int i = 0; i < message.length()*3 - 2; i++) {
			first = first.multiply(new BigInteger("1033303"));
			first = first.add(BigInteger.ONE);
			first = first.modPow(new BigInteger("1000000103"), HASHMAX);
			first = first.add(new BigInteger("17").multiply(new BigInteger(String.valueOf(Integer.valueOf(message.charAt((i + 7)%message.length()))))));
			first = first.flipBit(2 + i);
		}
		return first.toString();
	}
	
	private static String shuffle(String message) {

		String other = String.valueOf(message);
		
		for (int i = 0; i < message.length()/2; i++) {
			
			int temp = (i + 2*i)%message.length();
			int sec = message.length() - 1 - i;
			int med = (message.length() - i)/2;
			int four = ((i+1) * 5 - 3)%message.length();
			
			other = swap(message, temp, med);
			other = swap(message, sec, four);
			other = swap(message, med, four);
			other = swap(message, temp, sec);
		}
		return other;
	}
	
	public static String swap(String string, int element1, int element2) {
		if (element1 >= string.length() || element2 >= string.length()) return string;	
		if (element1 < 0 || element2 < 0) return string;
		if (element1 > element2) return string;
		if (element1 == element2) return string;

		String swapped = "";
		
		for (int i = 0; i < string.length(); i++) {
			
			if (i == element1) {
				swapped += string.charAt(element2);
			} else if (i == element2) {
				swapped += string.charAt(element1);
			} else {
				swapped += string.charAt(i);
			}
			
		}
		return swapped;
	}
	
	private static BigInteger String_To_Value(String string) {
		
		if (string == null || string.isEmpty()) {
			return BigInteger.ZERO;
		}
		
		BigInteger val = BigInteger.ONE;
		
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			String num = String.valueOf(Integer.valueOf(c));
			
			val = val.add(new BigInteger(num)).multiply(new BigInteger("1033"));
		}
		return val;
	}
}

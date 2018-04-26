/*
 * Networked Chat with RSA Encryption/Decryption
 * Project 5
 * CS 342 - Fall 2017
 * 
 * - Margi Katwala
 * - Bushra Baqui
 * - Aditya Sinha
 * 
 * **RSA.java**  
 */

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class RSA 
{	
	// mods the integers by a certain power
    public static LargeInteger modPower(final LargeInteger a, final LargeInteger p, final LargeInteger mod) 
    {
        LargeInteger returnNumber = new LargeInteger(1);
        LargeInteger valN = new LargeInteger(a);
        LargeInteger publicPrivate = new LargeInteger(p);
        
        while (LargeInteger.compare(publicPrivate, new LargeInteger(0)) > 0) 
        {
            if (!publicPrivate.isEven())
            {
                returnNumber.multiply(valN);
                if (returnNumber.toString().length() > 50 && LargeInteger.compare(returnNumber, mod) >= 0) returnNumber = returnNumber.mod(mod);
            }
            publicPrivate = LargeInteger.divideBy2(publicPrivate);
            valN.multiply(valN);

            if (valN.toString().length() > 50 && LargeInteger.compare(valN, mod) >= 0) 
            	valN = valN.mod(mod); 
        }
        return returnNumber.mod(mod);
    }

    // takes the inverse of the integer for deblocking...
    public static LargeInteger inverse(LargeInteger a, LargeInteger modNumber)
    {
        LargeInteger numberType = new LargeInteger(0);
        LargeInteger numberTypeLarge = new LargeInteger(1);
        LargeInteger remainderNumber = new LargeInteger(modNumber);
        LargeInteger remainderNumberLarge = new LargeInteger(a).mod(modNumber);
        
        if (LargeInteger.compare(modNumber, new LargeInteger(0)) < 0)
        {
            modNumber.setNegative(!modNumber.isNegativeNumber());
        }
        if (LargeInteger.compare(a, new LargeInteger(0)) < 0) 
        {
            LargeInteger newNumber = new LargeInteger(a);
            newNumber.setNegative(true);
            LargeInteger tmp = new LargeInteger(modNumber);
            tmp.subtract(newNumber.mod(modNumber));
            modNumber = tmp;
        }
        while (LargeInteger.compare(remainderNumberLarge, new LargeInteger(0)) != 0)
        {
            LargeInteger quotientNumber = new LargeInteger(remainderNumber).divide(remainderNumberLarge);
            LargeInteger largeNumber = new LargeInteger(numberTypeLarge);
            numberTypeLarge = new LargeInteger(numberType).subtract(new LargeInteger(quotientNumber).multiply(numberTypeLarge));
            numberType = new LargeInteger(largeNumber);
            largeNumber = new LargeInteger(remainderNumberLarge);
            remainderNumberLarge = new LargeInteger(remainderNumber).subtract(new LargeInteger(quotientNumber).multiply(remainderNumberLarge));
            remainderNumber = new LargeInteger(largeNumber);
        }
        if (LargeInteger.compare(remainderNumber, new LargeInteger(1)) > 0)
        {
            return new LargeInteger(-1);
        }
        if (LargeInteger.compare(numberType, new LargeInteger(0)) < 0)
        {
            numberType.add(modNumber);
        }
        return numberType;
    }

    // gets the random prime number from a file...
    public static LargeInteger random_prime(int bits) 
    {
        while (true) 
        {

            LargeInteger r = randomNumber(bits);
            BigInteger bigInteger = new BigInteger(r.toString());
            if (bigInteger.isProbablePrime(10)) 
            {
                return r;
            }
        }
    }
    
	public static Key getlocalKey() 
	{
		LargeInteger x = new LargeInteger(37);
	    LargeInteger xx = new LargeInteger(39);
	    return generateKeys(x,xx);
	}

	// gets the random integer
    public static LargeInteger randomNumber(int bits)
    {

        Random random = new Random();
        LargeInteger ret = new LargeInteger(0);
        for (int i = 0; i < bits; i++) 
        {
            if (random.nextBoolean()) 
            {
                ret.add(new LargeInteger(2).power(i));
            }
        }
        return ret;
    }

    // function to generate the public and private keys
    public static Key generateKeys(LargeInteger p, LargeInteger q) 
    {
        LargeInteger
                n = new LargeInteger(p).multiply(q),
                t = new LargeInteger(new LargeInteger(p).subtract(new LargeInteger(1))).multiply(new LargeInteger(q).subtract(new LargeInteger(1)));
        LargeInteger d;
        LargeInteger e;
        
        while (true) 
        {
            while (true) 
            {
                e = random_prime(5);
                if (LargeInteger.compare(new LargeInteger(t).mod(e), new LargeInteger(0)) == 0)
                    continue;
                if (LargeInteger.compare(e, t) < 0)
                    break;
            }
            d = inverse(e, t);

            if (LargeInteger.compare(d, new LargeInteger(-1)) != 0)
                break;
        }
        return new Key(e, d, n);
    }

    static ArrayList<LargeInteger> encrypt(String message, LargeInteger n, LargeInteger e, int block) {

        ArrayList<LargeInteger> unencrypted = blockString(message, block);
        ArrayList<LargeInteger> encrypted = new ArrayList<>();
        for (LargeInteger integer : unencrypted) {
            encrypted.add(encrypt(integer, n, e));
        }
        return encrypted;
    }

    // blocks the message string that is being sent...
    public static ArrayList<LargeInteger> blockString(String message, int block)
    {
        String currentLength = "";
        ArrayList<LargeInteger> values = new ArrayList<>();
        for (int i = 1; i <= message.length(); i++) 
        {
            currentLength += message.charAt(i - 1);
            if ((i) % block == 0) {
                LargeInteger unencrypted = convertToInt(currentLength);
                values.add(unencrypted);
                currentLength = "";
            }

        }
        if (currentLength.length() > 0) 
        {
            while (currentLength.length() < block) 
            {
                currentLength += (char) 0;
            }
            LargeInteger unencrypted = convertToInt(currentLength);
            values.add(unencrypted);

        }
        return values;
    }

    // decrypts the string...
    static String decrypt(ArrayList<LargeInteger> values, LargeInteger n, LargeInteger d, int block)
    {

        ArrayList<LargeInteger> unencrypted = new ArrayList<>();
        for (LargeInteger value : values) 
        {
            unencrypted.add(decrypt(value, n, d));
        }
        return unblock(unencrypted, block);
    }

    static String unblock(ArrayList<LargeInteger> values, int block) 
    {
        String message = "";
        for (LargeInteger value : values) 
        {
            String curr = "";
            for (int i = 1; i <= block; i++)
            {
                char c = (char) value.mod(new LargeInteger(100)).toInt();
                value.divide(new LargeInteger(100));
                if (c != 0) {
                    curr += (char) getDecoding(c);
                }
            }
            message += curr;
        }
        return message;
    }

    // gets the encoding for the characters... 
    private static int getEncoding(char encodeCharacter)
    {
        if(encodeCharacter==0)
            return 0;
        else if(encodeCharacter==11)
            return 1;
        else if(encodeCharacter==9)
            return 2;
        else if(encodeCharacter==10)
            return 3;
        else if(encodeCharacter==13)
            return 4;
        else if(encodeCharacter==' ')
            return 5;
        else return encodeCharacter-27;
    }

    // gets the decoding for the characters...
    private static int getDecoding(char decodeCharacter)
    {
        if(decodeCharacter==0)
            return 0;
        else if(decodeCharacter==1)
            return 11;
        else if(decodeCharacter==2)
            return 9;
        else if(decodeCharacter==3)
            return 10;
        else if(decodeCharacter==4)
            return 13;
        else if(decodeCharacter==5)
            return ' ';
        else return decodeCharacter+27;
    }
    
    // converts the string to an integer...
    public static LargeInteger convertToInt(String m) 
    {
        LargeInteger t = new LargeInteger();
        for (int i = 0; i < m.length(); i++)
        {
            t.add(new LargeInteger(getEncoding(m.charAt(i))).multiply(new LargeInteger(100).power(i)));
        }
        return t;
    }

    // encrypts using mod
    public static LargeInteger encrypt(LargeInteger m, LargeInteger n, LargeInteger e)
    {
        return modPower(m, e, n);
    }

    // decrypts using mod
    public static LargeInteger decrypt(LargeInteger m, LargeInteger n, LargeInteger d) 
    {
        return modPower(m, d, n);
    }

    // reads the file
    static String readFile(String path, Charset encoding)
            throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static class Key 
    {
        public LargeInteger e, d, n;
        public Key(LargeInteger e, LargeInteger d, LargeInteger n) 
        {
            this.e = e;
            this.d = d;
            this.n = n;
        }

        @Override
        public String toString() 
        {
            return "Key{" +
                    "e=" + e +
                    ", d=" + d +
                    ", n=" + n +
                    '}';
        }
    }
}

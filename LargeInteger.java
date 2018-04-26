/*
 * Networked Chat with RSA Encryption/Decryption
 * Project 5
 * CS 342 - Fall 2017
 * 
 * - Margi Katwala
 * - Bushra Baqui
 * - Aditya Sinha
 * 
 * **LargeInteger.java**  
 */

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class LargeInteger 
{
    List<Integer> integerValue;
    boolean negativeNumber;

    public LargeInteger() 
    {
        negativeNumber = false;
        integerValue = new ArrayList<>();
    }

    public LargeInteger(String str) 
    {
        integerValue = new ArrayList<>();
        // value for the integers start with -1
        negativeNumber = str.startsWith("-1");
        for (int i = 0; i < str.length(); i++) 
        {
            if (Character.isDigit(str.charAt(i))) 
            {
                integerValue.add(str.charAt(i) - '0');
            }
        }
        Collections.reverse(integerValue);
    }

    public LargeInteger(List<Integer> values, boolean negative) 
    {
        this.integerValue = values;
        this.negativeNumber = negative;
    }

    public LargeInteger(int n) 
    {
        negativeNumber = false;
        if (n < 0) 
        {
            negativeNumber = true;
            n = -n;
        }
        integerValue = new ArrayList<>();
        while (n > 0) 
        {
            int rem = n % 10;
            n /= 10;
            integerValue.add(rem);
        }
    }

    public LargeInteger(LargeInteger n) 
    {
        integerValue = new ArrayList<>();
        for (int v : n.getIntegerValue()) 
        {
            integerValue.add(v);
        }
        negativeNumber = n.isNegativeNumber();
    }

    static LargeInteger divideBy2(LargeInteger a) 
    {
        List<Integer> endList = new ArrayList<>();
        int lastNumber = 0;
        boolean firstNumber = false;
        for (int i = a.getIntegerValue().size() - 1; i >= 0; i--)
        {

            if (!firstNumber) 
            {
                if (a.getIntegerValue().get(i) == 0)
                    continue;
            }
            firstNumber = true;
            int tmp = a.getIntegerValue().get(i) + 10 * lastNumber;
            int v = tmp / 2;

            endList.add(v);
            lastNumber = tmp % 2;
        }
        Collections.reverse(endList);
        trimLeadingZeros(endList);

        return new LargeInteger(endList, a.isNegativeNumber());
    }

    private static void trimLeadingZeros(List<Integer> newList) 
    {
        while (newList.size() > 0) 
        {
           if (newList.get(newList.size() - 1) == 0)
               newList.remove(newList.size() - 1);
           else
               break;
        }
    }

    private static int absCompare(LargeInteger a, LargeInteger b) 
    {
        for (int i = Math.max(a.getIntegerValue().size(), b.getIntegerValue().size()) - 1; i >= 0; i--) 
        {
            int t1 = ((i < a.getIntegerValue().size()) ? a.getIntegerValue().get(i) : 0);
            int t2 = ((i < b.getIntegerValue().size()) ? b.getIntegerValue().get(i) : 0);
            if (t1 < t2)
                return -1;
            else if (t2 < t1)
                return 1;
        }
        return 0;
    }

    // compares the two integers...
    public static int compare(LargeInteger a, LargeInteger b) 
    {
        if (!a.isNegativeNumber() && !b.isNegativeNumber())
            return absCompare(a, b);
        else if (a.isNegativeNumber() && b.isNegativeNumber()) 
        {
            return -absCompare(a, b);
        } 
        else if (a.isNegativeNumber())
        {
            return -1;
        }
        else if (b.isNegativeNumber())
        {
            return 1;
        }

        return 0;
    }

    public List<Integer> getIntegerValue() 
    {
        return integerValue;
    }

    public boolean isNegativeNumber() 
    {
        return negativeNumber;
    }

    public void setNegative(boolean negative) 
    {
        this.negativeNumber = negative;
    }

    // adding the large integers...
    void add(LargeInteger from) 
    {
        if (!from.isNegativeNumber() && !isNegativeNumber()) 
        {
            List<Integer> fromList = from.getIntegerValue();
            int index = 0;
            int remainingNumber = 0;
            List<Integer> newList = new ArrayList<>();
            
            for (index = 0; index < fromList.size() && index < integerValue.size(); index++) 
            {
                int a = fromList.get(index) + integerValue.get(index) + remainingNumber;
                newList.add(a % 10);
                remainingNumber = a / 10;
            }
            for (; index < fromList.size(); index++) 
            {
                int a = fromList.get(index) + remainingNumber;
                newList.add(a % 10);
                remainingNumber = a / 10;
            }
            for (; index < integerValue.size(); index++) 
            {
                int a = integerValue.get(index) + remainingNumber;
                newList.add(a % 10);
                remainingNumber = a / 10;
            }
            
            if (remainingNumber > 0) 
            {
                newList.add(remainingNumber);
            }
            integerValue = newList;
        } 
        
        else if (from.isNegativeNumber() && isNegativeNumber()) 
        {
            from.setNegative(false);
            setNegative(false);
            add(from);
            setNegative(true);
        } 
        else if (absCompare(from, this) == 0) 
        {
            integerValue = new ArrayList<>();
            negativeNumber = false;
        }
        else if (isNegativeNumber()) 
        {
            if (absCompare(this, from) > 0) 
            { 
                LargeInteger t = subHelper(this, from);
                integerValue = t.getIntegerValue();
                negativeNumber = true;
            } 
            else 
            {
                LargeInteger t = subHelper(from, this);
                integerValue = t.getIntegerValue();
                negativeNumber = false;
            }
        }
        else
	        {
	            if (absCompare(this, from) > 0)
		            {
		                LargeInteger t = subHelper(this, from);
		                integerValue = t.getIntegerValue();
		                negativeNumber = false;
		            } 
	            else
		            {
		                LargeInteger t = subHelper(from, this);
		                integerValue = t.getIntegerValue();
		                negativeNumber = true;
		            }
	        }
    }

    // multiplies the large integers...
    LargeInteger multiply(LargeInteger a) 
    {
        if (a.getIntegerValue().size() == 0 || getIntegerValue().size() == 0) 
        {
            setNegative(false);
            integerValue = new ArrayList<>();
        }
        LargeInteger answer = new LargeInteger();
        for (int i = 0; i < a.getIntegerValue().size(); i++) 
        {
            ArrayList<Integer> create = new ArrayList<>();
            int remaining = 0;
            for (int j = 0; j < integerValue.size(); j++) 
            {
                remaining = a.getIntegerValue().get(i) * getIntegerValue().get(j) + remaining;
                create.add(remaining % 10);
                remaining /= 10;
            }
            create.add(remaining);
            for (int j = 0; j < i; j++) 
            {
                create.add(0, 0);
            }
            answer.add(new LargeInteger(create, false));
        }
        integerValue = answer.getIntegerValue();
        setNegative(negativeNumber != a.isNegativeNumber());
        return this;
    }

    // mods the large integers...
    LargeInteger modMultiply(LargeInteger a, LargeInteger mod) 
    {
        if (a.getIntegerValue().size() == 0 || getIntegerValue().size() == 0) 
        {
            setNegative(false);
            integerValue = new ArrayList<>();
        }
        LargeInteger result = new LargeInteger();
        for (int i = 0; i < a.getIntegerValue().size(); i++) 
        {
            ArrayList<Integer> aList = new ArrayList<>();
            int remNum = 0;
            for (int j = 0; j < integerValue.size(); j++) 
            {
                remNum = a.getIntegerValue().get(i) * getIntegerValue().get(j) + remNum;
                aList.add(remNum % 10);
                remNum /= 10;
            }
            aList.add(remNum);
            for (int j = 0; j < i; j++) 
            {
                aList.add(0, 0);
            }
            LargeInteger t = new LargeInteger(aList, false);
            t = t.mod(mod);
            result.add(t);
            result = result.mod(mod);
        }
        integerValue = result.getIntegerValue();
        setNegative(negativeNumber != a.isNegativeNumber());
        return this;
    }

    // divides the large integers...
    LargeInteger divide(LargeInteger a) 
    {
        if (absCompare(a, this) > 0) 
        {
            integerValue = new ArrayList<>();
            negativeNumber = false;
        } 
        else if (absCompare(a, this) == 0) 
        {
            integerValue = new ArrayList<>();
            integerValue.add(1);
            negativeNumber = isNegativeNumber() != a.isNegativeNumber();
        } 
        else 
        {
            negativeNumber = isNegativeNumber() != a.isNegativeNumber();
            LargeInteger lowNumber = new LargeInteger(1);
            LargeInteger highNumber = new LargeInteger(this);
            highNumber.setNegative(false);
            lowNumber.setNegative(false);
            while (absCompare(highNumber, lowNumber) > 0) 
            {
                LargeInteger newNumber = new LargeInteger(highNumber);
                newNumber.add(lowNumber);
                LargeInteger middleNumber = divideBy2(newNumber);
                newNumber = new LargeInteger(middleNumber);
                newNumber.multiply(a);

                if (absCompare(newNumber, this) <= 0) 
                {
                    LargeInteger newLargeNum = new LargeInteger(middleNumber);
                    newLargeNum.add(new LargeInteger(1));
                    newLargeNum.multiply(a);
                    if (absCompare(newLargeNum, this) > 0) 
                    {
                        integerValue = middleNumber.getIntegerValue();
                        return this;
                    }
                }

                if (absCompare(newNumber, this) > 0)
                {
                    highNumber = middleNumber;

                } 
                else
                {
                    lowNumber = middleNumber;
                    lowNumber.add(new LargeInteger(1));

                }
            }
        }
        return this;
    }

    // parses the integers
    public int toInt() 
    {
        return Integer.parseInt(toString());
    }

    LargeInteger mod(LargeInteger a) 
    {
        LargeInteger modNumber = new LargeInteger(this);
        modNumber.divide(a);
        LargeInteger multiplyNumber = new LargeInteger(a);
        multiplyNumber.multiply(modNumber);
        LargeInteger subtractNumber = new LargeInteger(this);
        subtractNumber.subtract(multiplyNumber);
        return subtractNumber;
    }

    public boolean isEven() 
    {
        return integerValue.size() == 0 || integerValue.get(0) % 2 == 0;
    }

    public LargeInteger subtract(LargeInteger a) 
    {
        LargeInteger t = new LargeInteger(a);
        t.setNegative(!a.isNegativeNumber());
        add(t);
        return this;
    }

    // helper function for the subtraction function...
    private LargeInteger subHelper(LargeInteger a, LargeInteger b) 
    {
        int carryNumber = 0;
        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i < Math.max(a.getIntegerValue().size(), b.getIntegerValue().size()); i++) 
        {
            int result = ((i < a.integerValue.size()) ? a.integerValue.get(i) : 0) -
                    ((i < b.getIntegerValue().size()) ? b.getIntegerValue().get(i) : 0) - carryNumber;
            if (result < 0)
	            {
	                newList.add(result + 10);
	                carryNumber = 1;
	            }
            else 
	            {
	                newList.add(result);
	                carryNumber = 0;
	            }
        }
        return new LargeInteger(newList, carryNumber > 0);
    }

    @Override
    public String toString() 
    {
        if (integerValue.size() == 0) 
        {
            return "0";
        }
        trimLeadingZeros(integerValue);
        String ret = "";
        if (negativeNumber)
            ret = "-";
        for (int i = integerValue.size() - 1; i >= 0; i--)
            ret += integerValue.get(i);
        return ret;
    }

    LargeInteger power(int p) 
    {
        LargeInteger returnInteger = new LargeInteger(1);
        LargeInteger newNumber = new LargeInteger(this);
        while (p > 0) 
        {
            if (p % 2 == 1) 
            {
                returnInteger.multiply(newNumber);
            }

            p = p / 2;
            newNumber.multiply(newNumber);
        }
        trimLeadingZeros(returnInteger.getIntegerValue());
        return returnInteger;
    }
}

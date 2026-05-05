package jchat.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigFileParser
{
    private HashMap<String, String> valueMap;

    public ConfigFileParser(String path)
    {
        valueMap = new HashMap<>();
        try
        {
            File file = new File(path);
            Scanner fileScanner = new Scanner(file);

            String currentLine;

            while(fileScanner.hasNextLine()) {
                currentLine = fileScanner.nextLine();
                Scanner lineScanner = new Scanner(currentLine);

                //Parse Key
                StringBuilder key = new StringBuilder(lineScanner.next());
                key.deleteCharAt(key.indexOf(":"));

                //Parse Value
                StringBuilder value = new StringBuilder(lineScanner.next());

                while(lineScanner.hasNext())
                {
                    value.append(" ");
                    value.append(lineScanner.next());
                }

                //Clean Quotation marks from value if present
                if(value.charAt(value.length() - 1) == ('\"'))
                {
                    value.deleteCharAt(value.length() - 1);
                }

                if(value.charAt(0) == ('\"'))
                {
                    value.deleteCharAt(0);
                }



                valueMap.put(key.toString(), value.toString());
            }

            System.out.println(valueMap);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean getBoolean(String key)
    {
        return Boolean.parseBoolean(valueMap.get(key));
    }


    public byte getByte(String key)
    {
        return Byte.parseByte(valueMap.get(key));
    }


    public short getShort(String key)
    {
        return Short.parseShort(valueMap.get(key));
    }


    public int getInteger(String key)
    {
        return Integer.parseInt(valueMap.get(key));
    }

    public float getFloat(String key)
    {
        return Float.parseFloat(valueMap.get(key));
    }

    public double getDouble(String key)
    {
        return Double.parseDouble(valueMap.get(key));
    }

    public String getString(String key)
    {
        return valueMap.get(key);
    }



}

package dcprototype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Options 
{ // get the first argument from args
  public static String getFirst(String[] args){ return args[0];}
  // return the args except the first argument
  public static String[] removeFirst(String[] args)
  { String[] res = new String[args.length-1];
    System.arraycopy(args, 1, res, 0, args.length-1);
    return res;
  } 
  // check whether args contain the option or not.       
  // if args contain the option, return true, otherwise return false
  public static boolean isSet(String[] args, String option)
  { for (int i = 0; i < args.length; i++)
      if(args[i].equals(option)) return true;
    return false;
  }  
  // return new args without any "option" in the args
  public static String[] remove(String[] args, String option)
  { int n = 0;
    for(int i = 0; i <  args.length; i++)
      if(args[i].equals(option)) n++;
    if(n == 0) return args;
    String res[] = new String[args.length-n];
    int k = 0;
    for(int i = 0; i < args.length; i++)
    { if(!args[i].equals(option))
        res[k++]=args[i];
    }    
    return res;
  }    
  // get the argument after the option in args
  // e.g. when option="-source" and this method will return the string after -source
  // if there is no such option in this args, it will return null
  public static String getOption(String[] args, String option)
  { for(int i = 0; i <  args.length-1; i++)
      if(args[i].equals(option)) return args[i+1];
    return null;// when args don't contain the option
  }     
  // return new args without the option and the parameter after the option.
  // e.g. when option = "-source" parameter = sourceFileName
  // this method will return the rest args without -source sourcFileName
  public static String[] remove(String[] args, String option, String parameter)
  { // return the args, if args don't contain the option
    if(getOption(args, option)==null) return args;    
    String res[] = new String[args.length-2];
    for(int i=0, j=0; i<args.length; i++, j++)
      if(args[i].equals(option) && 
         i+1<args.length && 
         (parameter == null || args[i+1].equals(parameter))
        )
      { i++; j--;}//skip parameters
      else res[j]=args[i];
    return res;
  }  
       
  public static String getFileContentsAsString(String filePathName)
  { StringBuilder buf=new StringBuilder();
    try
    { BufferedReader in = new BufferedReader(new FileReader(new File(filePathName)));
      for(String line=in.readLine(); line!=null; line=in.readLine())
        if(line.startsWith("#")) System.out.println(line);
        else buf.append(line).append("\n");
      in.close();
    }    
    catch(IOException e){}
    return buf.toString();
  }
  public static List<String> getFileContentsAsListOfStrings(String filePathName)
  { List<String> lines = new ArrayList<String>();
    try
    { BufferedReader in = new BufferedReader(new FileReader(new File(filePathName)));
      for(String line = in.readLine(); line!=null; line=in.readLine())
        if(line.startsWith("#")) System.out.println(line);
        else lines.add(line);
      in.close();
    }
    catch(IOException e){}
    return lines;
  }     
}

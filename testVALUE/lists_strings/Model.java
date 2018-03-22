import java.io.BufferedReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Model 
{ public static void main(String[] args) throws IOException 
  { constructFileContent("source1.java");
    constructFileContent("target1.java");
    constructFileContent("source3.java");
    constructFileContent("target3.java");
    constructFileContent("source14.java");
    constructFileContent("target14.java");
  }

  private static void constructFileContent(String fileName)
  { try
    { File newFile = new File("constructed_"+fileName);
      if(!newFile.exists()) newFile.createNewFile();
      BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
      BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
      
      writer.write("[\"");
      String line=reader.readLine();
      if(line!=null)
      { writer.write(line);
        writer.write("\"");
      }
      for(line=reader.readLine(); line!=null; line=reader.readLine())
      { writer.write(",\"");
        writer.write(line);
        writer.write("\"");
      }
      writer.write("]");
      reader.close();
      writer.close();
    }    
    catch(IOException e){}
  }
}

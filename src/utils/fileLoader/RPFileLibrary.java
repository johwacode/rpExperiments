package utils.fileLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class RPFileLibrary {
	public static void writeToFile(String foldername, String filename, Serializable content){
		File folder = new File(foldername);
		folder.mkdirs();
	    try {
	    	OutputStream buffer = new BufferedOutputStream(new FileOutputStream(foldername+"/"+filename));
		    ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(content);
			output.close();
			System.out.println("Data written into "+filename+".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Serializable readFile(String filename){
		Serializable res = null;
		try{
			InputStream buffer = new BufferedInputStream(new FileInputStream(filename));
			ObjectInput input = new ObjectInputStream (buffer);
			res = (Serializable) input.readObject();
			input.close();
		} catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}
}

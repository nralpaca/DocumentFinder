package mydocfinder;

import java.io.FileInputStream;
import java.util.Properties;

 

public class Main {
	public static void main(String args[]) throws Exception {
        String propFile = "config.properties";
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propFile);
        props.load(new java.io.BufferedInputStream(fis));

        for(Object key : props.keySet()){
            System.out.println(key + " : " + props.getProperty((String)key));
        }
		
		UserInterface ui = new UserInterface();
		
		ui.display();
	}
}

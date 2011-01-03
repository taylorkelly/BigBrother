import java.io.File;
import java.io.FilenameFilter;


public class LogFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {
		if(arg1.endsWith(".txt")) return true;
		return false;
	}

}

package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import database.ReturnObject;


public class Warnings {
	void warning(DataInputStream input, DataOutputStream output, ReturnObject returnObject) throws IOException {
		ArrayList<String> warnings = returnObject.getWarning();
		if (warnings.isEmpty()) {

		} else {
			ServerGUI.print('\n'+"warnings: ");
			for (String w : warnings) {
				ServerGUI.print(w);
			}
			if (warnings.contains("Old Hash Method")) {
				// force new PW
				changePW();
			}
			ServerGUI.print("");
		}
	}
	
	private void changePW() {
		
	}
	
}

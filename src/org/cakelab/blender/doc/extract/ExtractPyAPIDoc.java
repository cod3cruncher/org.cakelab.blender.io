package org.cakelab.blender.doc.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.cakelab.blender.doc.Documentation;
import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONObject;

public class ExtractPyAPIDoc extends Documentation {
	private BufferedReader in;
	public ExtractPyAPIDoc(File file, String version, File out) throws IOException {
		in = new BufferedReader(new FileReader(file));
		
		includePath = null;
		system = "Blender";
		module = "DNA";
		super.source = "Blender Python API";
		this.version = version;

		this.structdocs = new JSONObject();
		
		String line;
		while (null != (line = in.readLine())) {
			parse(line);
		}
		
		super.write(out);
		
	}

	private void parse(String line) {
		//
		// BaseClass|SubClass.member -> sdnaMember:   type   documentation<NL>
		//
		
		
		line = line.trim();
		int i = line.indexOf(":");
		if (i<0) return; // empty line
		String name = line.substring(0, i);
		String value = line.substring(i+1);
		
		//
		// parse name portion
		//
		String[] structNames = name.split("\\|");
		name = structNames[structNames.length-1];

		String[] baseStructNames = Arrays.copyOf(structNames, structNames.length-1);
		
		i = name.indexOf(".");
		String structName = name.substring(0, i);
		String sdnaFieldPrefix = " -> ";
		i = name.indexOf(sdnaFieldPrefix);
		String memberName = name.substring(i+sdnaFieldPrefix.length()).trim();
		
		//
		// parse value portion
		//
		value = value.trim();
		i = value.indexOf(" ");
		if (i<0) return; // no doc
		
		String type = value.substring(0, i);
		
		String documentation = value.substring(i).trim();
		
		
		addFieldDoc(baseStructNames, structName, memberName, documentation);
		
	}

	public void addFieldDoc(String[] baseStructNames, String structName, String memberName,
			String documentation) {
		JSONObject struct = (JSONObject) structdocs.get(structName);
		if (struct == null) {
			struct = new JSONObject();
			structdocs.put(structName, struct);
		}
		
		if (baseStructNames.length > 0) {
			JSONArray baseclasses = new JSONArray();
			struct.put("inherits", baseclasses);
			for (String baseStructName : baseStructNames) {
				baseclasses.add(baseStructName);
			}
		}
		JSONObject fieldsdoc = (JSONObject) struct.get("fields");
		if (fieldsdoc == null) {
			fieldsdoc = new JSONObject();
			struct.put("fields", fieldsdoc);
		}

		fieldsdoc.put(memberName, documentation);
	}

	public static void main(String[] args) throws IOException {

		String version = "2.69";
		ExtractPyAPIDoc extractor = new ExtractPyAPIDoc(
				new File("/tmp/pyapidoc-" + version + ".txt"), 
				version, 
				new File("resources/dnadoc/" + version + "/pyapi/doc.json"));
		
	}

}
package org.cakelab.blender.generator;

import java.io.File;
import java.io.IOException;

import org.cakelab.blender.doc.Documentation;
import org.cakelab.blender.doc.DocumentationProvider;
import org.cakelab.json.JSONException;

public class DocGenerator implements DocumentationProvider {
	// TODO: ZZZ doc generator needs to HTML encode <pre/> sections
	private Documentation[] docs;

	public DocGenerator(File[] docfiles, boolean debug) throws IOException, JSONException {
		docs = new Documentation[docfiles.length];
		int i = 0;
		for (File f : docfiles) {
			docs[i++] = new Documentation(f, debug);
		}
	}

	@Override
	public String getStructDoc(String struct) {
		StringBuffer lines = new StringBuffer();
		
		for (Documentation doc : docs) {
			String docentry = doc.getStructDoc(struct);
			if (docentry != null && docentry.length() > 0) {
				lines.append("<h4>" + doc.getSource() + ":</h4>").append("\n");
				lines.append(docentry);
			}
		}
		return lines.toString();
	}

	@Override
	public String getFieldDoc(String struct, String field) {
		StringBuffer lines = new StringBuffer();
		
		for (Documentation doc : docs) {
			String docentry = doc.getFieldDoc(struct, field);
			if (docentry != null && docentry.length() > 0) {
				lines.append("<h4>" + doc.getSource() + ":</h4>").append("\n");
				lines.append(docentry);
			}
		}
		return lines.toString();
	}

}

package org.cakelab.blender.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.cakelab.blender.file.dna.BlendField;
import org.cakelab.blender.file.dna.BlendStruct;
import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.Parser;

public class Documentation {
	JSONObject structdocs;
	private String[] authors;

	private String system;
	private String module;
	private String version;
	private File includePath;

	public Documentation (File docfile) throws IOException, JSONException{
		JSONObject docjson = new Parser(new FileInputStream(docfile)).parse();
		
		includePath = docfile.getCanonicalFile().getParentFile();
		system = docjson.getString("system");
		module = docjson.getString("module");
		version = docjson.getString("version");
		JSONArray authors = (JSONArray) docjson.get("authors");
		if (authors != null) {
			this.authors = authors.toArray(new String[0]);
		}
		
		structdocs = new JSONObject();
		String filename = docjson.getString("inherits");
		if (filename != null) {
			try {
				structdocs = new Documentation(new File(includePath, filename)).structdocs;
			} catch (IOException | JSONException e) {
				System.err.println("Warning: couldn't read base documentation'" + filename + "' inherited by '" + docfile.getName() + "'.");
				System.err.println("reason: " + e.getMessage());
			}
		}
		
		JSONArray includeFiles = (JSONArray) docjson.get("includes");
		if (includeFiles != null) {
			for (int i = 0; i < includeFiles.size(); i++) {
				filename = (String)includeFiles.get(i);
				try {
					Documentation include = new Documentation(new File(includePath, filename));
					includeStructs(include.structdocs, filename);
				} catch (IOException | JSONException e) {
					System.err.println("Warning: couldn't read doc file '" + filename + "' included by '" + docfile.getName() + "'.");
					System.err.println("reason: " + e.getMessage());
				}
			}
		}
		
		JSONObject mydocs = (JSONObject) docjson.get("structs");
		if (mydocs != null) {
			extendStructs(mydocs, docfile.getName());
		}
		
		resolveInheritance();
	}

	private void extendStructs(JSONObject extensions, String source) {
		for (Entry<String, Object> structdocEntry : extensions.entrySet()) {
			String structname = structdocEntry.getKey();
			JSONObject structdoc = (JSONObject) structdocs.get(structname);
			
			if (structdoc != null) {
				overrideFields(structdoc, (JSONObject)structdocEntry.getValue());
			} else {
				structdocs.put(structname, structdocEntry.getValue());
			}
			
		}
	}

	private void overrideFields(JSONObject structdoc, JSONObject overrides) {
		JSONObject fields = (JSONObject) structdoc.get("fields");
		if (fields == null) {
			structdoc.put("fields", overrides.get("fields"));
		} else {
			JSONObject overridingFields = (JSONObject) overrides.get("fields");
			fields.putAll(overridingFields);
		}
		
	}

	private void includeStructs(JSONObject includes, String source) throws IOException {
		for (Entry<String, Object> structdoc : includes.entrySet()) {
			String structname = structdoc.getKey();
			if (structdocs.containsKey(structname)) {
				System.err.println("Warning: Include '" + source + "' is overriding existing entry for '" + structname + "' found in include.");
			} else {
				structdocs.put(structname, structdoc.getValue());
			}
			
		}
	}

	/**
	 * creates an empty documentation.
	 */
	public Documentation () {
		structdocs = null;
	}
	

	public String[] getAuthors() {
		return authors;
	}

	public String getSystem() {
		return system;
	}

	public String getModule() {
		return module;
	}

	public String getVersion() {
		return version;
	}

	public String getStructDoc(BlendStruct struct) {
		return getStructDoc(struct.getType().getName());
	}
	
	public String getFieldDoc(BlendStruct struct, BlendField field) {
		return getFieldDoc(struct.getType().getName(), field.getName());
	}
	
	private String getStructDoc(String structname) {
		String result = null;
		JSONObject structdoc = (JSONObject) structdocs.get(structname);
		if (structdoc != null) {
			result = structdoc.getString("doc");
		}
		return result;
	}
	
	private String getFieldDoc(String structname, String fieldname) {
		String result = null;
		if (structdocs == null)  return result;
		
		JSONObject structdoc = (JSONObject) structdocs.get(structname);
		if (structdoc != null) {
			JSONObject fieldsdoc = (JSONObject) structdoc.get("fields");
			if (fieldsdoc != null) {
				result = fieldsdoc.getString(fieldname);
			}
		}
	
		return result;
	}
	

	private void resolveInheritance() {
		for (Entry<String, Object> doc : structdocs.entrySet()) {
			resolveInheritance(doc.getKey());

		}
	}

	
	
	
	private JSONObject resolveInheritance(String structname) {
		JSONObject structdoc = (JSONObject) structdocs.get(structname);
		if (structdoc != null) {
			JSONArray inherits = (JSONArray) structdoc.get("inherits");
			if (inherits != null) {
				for (String base : inherits.toArray(new String[0])) {
					JSONObject basestructdoc = resolveInheritance(base);
					if (basestructdoc != null) inheritFields(structdoc, basestructdoc);
				}
			}
		} else {
			System.err.println("could not resolve inheritance of " + structname + ".");
			System.err.println("reason: " + structname + " is not defined");
		}
		return structdoc;
	}

	private void inheritFields(JSONObject structdoc, JSONObject base) {
		JSONObject basefields = (JSONObject) base.get("fields");
		if (basefields != null) {
			JSONObject myfields = (JSONObject) structdoc.get("fields");
			if (myfields == null) {
				structdoc.put("fields", basefields);
			} else {
				for (Entry<String, Object> entry : basefields.entrySet()) {
					if (!myfields.containsKey(entry.getKey())) {
						myfields.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
	}
	public static void main (String [] args) throws IOException, JSONException {
		Documentation doc = new Documentation(new File("resources/dnadoc/2.69/DNA_documentation.json"));
		
		System.out.println(doc.getStructDoc("ID"));
		System.out.println(doc.getFieldDoc("ID", "next"));
		System.out.println(doc.getFieldDoc("ID", "properties"));
		
	}

	public String getPath() {
		return includePath.toString();
	}


}

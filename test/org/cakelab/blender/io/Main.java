package org.cakelab.blender.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.cakelab.blender.generator.type.CStruct;
import org.cakelab.blender.generator.type.MetaModel;
import org.cakelab.blender.io.BlenderFile;
import org.cakelab.blender.io.Encoding;
import org.cakelab.blender.io.block.Block;
import org.cakelab.blender.io.block.BlockHeader;
import org.cakelab.blender.io.block.BlockTable;
import org.cakelab.blender.io.dna.BlendModel;

public class Main {
	
	
	
	public static void main(String[] args) throws IOException {
		BlenderFile blender = new BlenderFile(new File("cube.blend"));
		BlendModel model = blender.getBlenderModel();
		ArrayList<Block> blocks = blender.getBlocks();
		blender.close();
		
		Encoding encoding = blender.getEncoding();
		
		BlockTable blockTable = new BlockTable(encoding);
		blockTable.addAll(blocks);

		//
		// retrieve the meta model, which is encoding independent
		//
		MetaModel meta = new MetaModel(model);
		
		//
		// Create a new block to store 1 scene struct
		//
		CStruct struct = (CStruct) meta.getType("Scene");
		int size = struct.sizeof(encoding.getAddressWidth());
		Block block = blockTable.allocate(BlockHeader.CODE_SCE, size);

		// 
		// init the block to retrieve the scene struct
		//
		block.header.setSdnaIndex(struct.getSdnaIndex());
		
		//
		// get a facet of type Scene
		// 
		
	}
}

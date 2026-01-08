package com.ganainy.motoman;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import java.util.ArrayList;


public class InstancingModel {
	private Model model;
	private Mesh[] meshes;
	private int[] nIndicesPerCopy;
	public int copies;
	
	public InstancingModel(Model model, int copies) {
		this.model = model;
		this.copies = copies;
        // 使用新的 Node 架构访问 mesh parts
        ArrayList<MeshPart> meshPartsList = new ArrayList<MeshPart>();
        for (Node node : model.nodes) {
            for (NodePart nodePart : node.parts) {
                meshPartsList.add(nodePart.meshPart);
            }
        }

        MeshPart[] meshParts = meshPartsList.toArray(new MeshPart[0]);
        meshes = new Mesh[meshParts.length];
        nIndicesPerCopy = new int[meshParts.length];

        for (int i = 0; i < meshes.length; ++i) {
            VertexAttributes vas = meshParts[i].mesh.getVertexAttributes();
            VertexAttribute[] vaa = new VertexAttribute[vas.size()];
            for (int j = 0; j < vaa.length; ++j) vaa[j] = vas.get(j);
            meshes[i] = new Mesh(true,
                    meshParts[i].mesh.getNumVertices() * copies,
                    meshParts[i].mesh.getNumIndices() * copies, vaa);

            int nVertex = meshParts[i].mesh.getNumVertices();
            int sizeVertex = meshParts[i].mesh.getVertexSize() / 4;
            float[] vertices = new float[nVertex * sizeVertex];
            float[] vertices2 = new float[nVertex * sizeVertex * copies];
            meshParts[i].mesh.getVertices(vertices);
            int nIndices = meshParts[i].mesh.getNumIndices();
            short[] indices = new short[nIndices];
            short[] indices2 = new short[nIndices * copies];
            meshParts[i].mesh.getIndices(indices);
			
			nIndicesPerCopy[i] = nIndices;
			
			VertexAttribute skeAttr = null;
			int skeIdOffset = 0;
			for (int k = 0; k < vaa.length; ++k) {
				if (vaa[k].alias.equals("a_skeleton")) {
					skeAttr = vaa[k];
					skeIdOffset = vaa[k].offset / 4;
					break;
				}
			}
			
			int maxSkeId = 0;
			if (skeAttr != null) {
				for (int k = 0; k < nVertex; ++k) {
					int skeId = (int)Math.round(vertices[k * sizeVertex + skeIdOffset]);
					if (skeId > maxSkeId) maxSkeId = skeId;
				}
			}
			
			for (int k = 0; k < copies; ++k) {
				System.arraycopy(vertices, 0, vertices2, k * vertices.length, vertices.length);
				System.arraycopy(indices, 0, indices2, k * nIndices, indices.length);
				for (int l = 0; l < nIndices; ++l)
					indices2[k * nIndices + l] += k * nVertex;
				if (skeAttr != null) {
					for (int l = 0; l < nVertex; ++l) {
						int skeId = (int)Math.round(vertices2[k * vertices.length + l * sizeVertex + skeIdOffset]);
						if (skeId != 0)
							vertices2[k * vertices.length + l * sizeVertex + skeIdOffset] = skeId + k * maxSkeId;
					}
				}
			}
			
			meshes[i].setVertices(vertices2);
			meshes[i].setIndices(indices2);
		}
	}

    public void render(ShaderProgram program, int nInst) {
        // 使用新的 Node 架构访问 mesh parts 和 materials
        ArrayList<NodePart> nodePartsList = new ArrayList<NodePart>();
        for (Node node : model.nodes) {
            for (NodePart nodePart : node.parts) {
                nodePartsList.add(nodePart);
            }
        }

        NodePart[] nodeParts = nodePartsList.toArray(new NodePart[0]);

        for (int i = 0; i < meshes.length; i++) {
            NodePart nodePart = nodeParts[i];
            MeshPart meshPart = nodePart.meshPart;

            // 材质绑定在新版本中需要手动处理
            // 这里可以添加手动设置材质属性的代码，或者移除材质绑定

            meshes[i].render(program, meshPart.primitiveType, 0, nInst * nIndicesPerCopy[i]);
        }
    }

	
	public void dispose() {
		for (int i = 0; i < meshes.length; ++i)
			meshes[i].dispose();
	}
}

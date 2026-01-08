package com.ganainy.motoman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
//import com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;


public class SkyBox {
	private static Model model;
	private static IMeshContext modelMeshContext;
	private static Model modelPreBloomed;
	private static IMeshContext modelPreBloomedMeshContext;
	private static Color bloomColor;
	private static Matrix4 scaleMatrix = new Matrix4();
	
	public static void initResource() {
		ObjLoaderEx objLoader = new ObjLoaderEx();
		model = objLoader.loadObj(Gdx.files.internal("data/skybox.obj"), true);
		modelPreBloomed = objLoader.loadObj(Gdx.files.internal("data/skybox.obj"), true);
		
		float scale = 1;
		scaleMatrix.scale(scale, scale, scale);
		
		bloomColor = new Color(0.3f, 0.3f, 1, 1);
		//bloomColor = new Color(1f, 0.3f, 0.3f, 1);
		
		preBloomModel();
		
		StaticModelTextureFilterConfigManager.add(model);
		StaticModelTextureFilterConfigManager.add(modelPreBloomed);
		
		modelMeshContext = MeshOptimized.globalStaticMesh.add(model);
		modelPreBloomedMeshContext = MeshOptimized.globalStaticMesh.add(modelPreBloomed);
	}
	
	public SkyBox() {
	}
	
	public void resume() {
		StaticModelTextureFilterConfigManager.remove(model);
		StaticModelTextureFilterConfigManager.remove(modelPreBloomed);
		preBloomModel();
		StaticModelTextureFilterConfigManager.add(model);
		StaticModelTextureFilterConfigManager.add(modelPreBloomed);
	}

    private static void preBloomModel() {
        Color tmpColor = new Color();

        // 遍历 Model 的所有 Node 来找到 NodePart
        for (Node node : model.nodes) {
            for (NodePart nodePart : node.parts) {
                MeshPart bloomingMeshPart = nodePart.meshPart;
                Material bloomingMaterial = nodePart.material;

                // 在第二个模型中找到对应的 MeshPart 和 Material
                for (Node node2 : modelPreBloomed.nodes) {
                    for (NodePart nodePart2 : node2.parts) {
                        MeshPart bloomedMeshPart = nodePart2.meshPart;
                        Material bloomedMaterial = nodePart2.material;

                        // 通过 meshPart.id 匹配
                        if (bloomingMeshPart.id.equals(bloomedMeshPart.id)) {
                            int nAttr = bloomingMaterial.size();
                            for (int i = 0; i < nAttr; ++i) {
                                Attribute attrBlooming = bloomingMaterial.get(i);
                                Attribute attrBloomed = bloomedMaterial.get(i);
                                if (attrBlooming instanceof TextureAttribute) {
                                    TextureAttribute tattrBlooming = (TextureAttribute)attrBlooming;
                                    TextureAttribute tattrBloomed = (TextureAttribute)attrBloomed;
                                    TextureData tdata = tattrBlooming.textureDescription.texture.getTextureData();
                                    tdata.prepare();
                                    Pixmap m = tdata.consumePixmap();
                                    int w = m.getWidth();
                                    int h = m.getHeight();
                                    Pixmap m2 = new Pixmap(w, h, m.getFormat());
                                    for (int y = 0; y < h; ++y)
                                        for (int x = 0; x < w; ++x) {
                                            Color.rgba8888ToColor(tmpColor, m.getPixel(x, y));
                                            tmpColor.r += bloomColor.r; if (tmpColor.r > 1) tmpColor.r = 1;
                                            tmpColor.g += bloomColor.g; if (tmpColor.g > 1) tmpColor.g = 1;
                                            tmpColor.b += bloomColor.b; if (tmpColor.b > 1) tmpColor.b = 1;
                                            m2.drawPixel(x, y, Color.rgba8888(tmpColor));
                                        }
                                    if (tdata.disposePixmap()) m.dispose();
                                    if (!tattrBloomed.textureDescription.texture.isManaged()) tattrBloomed.textureDescription.texture.dispose();
                                    tattrBloomed.textureDescription.texture = new Texture(m2, false);
                                    tattrBloomed.textureDescription.texture.setFilter(tattrBlooming.textureDescription.texture.getMinFilter(), tattrBlooming.textureDescription.texture.getMagFilter());
                                    tattrBloomed.textureDescription.texture.setWrap(tattrBlooming.textureDescription.texture.getUWrap(), tattrBlooming.textureDescription.texture.getVWrap());
                                    m2.dispose();
                                }
                            }
                            break; // 找到匹配后跳出内层循环
                        }
                    }
                }
            }
        }
    }
	
	public Color getSkyBloomColor() {
		return bloomColor;
	}
	
	private Matrix4 tmpMat = new Matrix4();
	public void render(ShaderProgram shader, Camera camera, boolean bloomEnabled) {
		tmpMat.set(camera.combined);
		tmpMat.translate(camera.position.x, camera.position.y, camera.position.z);
		tmpMat.mul(scaleMatrix);
		shader.setUniformMatrix("modelviewproj", tmpMat);
		tmpMat.set(camera.view);
		tmpMat.translate(camera.position.x, camera.position.y, camera.position.z);
		tmpMat.mul(scaleMatrix);
		shader.setUniformMatrix("modelview", tmpMat);
		if (bloomEnabled)
			modelMeshContext.render(shader);
		else
			modelPreBloomedMeshContext.render(shader);
	}
	
	public void dispose() {
	}
}

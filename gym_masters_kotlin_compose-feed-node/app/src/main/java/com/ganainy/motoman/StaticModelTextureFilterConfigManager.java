package com.ganainy.motoman;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;

public class StaticModelTextureFilterConfigManager {
	private static ArrayList<Texture> textures = new ArrayList<Texture>();
	
	public static void add(Texture t) {
		textures.add(t);
	}
	
	public static void remove(Texture t) {
		textures.remove(t);
	}

    public static void add(Model m) {
        // 遍历 Model 的所有 Node 来找到 NodePart
        for (Node node : m.nodes) {
            for (NodePart nodePart : node.parts) {
                Material material = nodePart.material;
                int na = material.size();
                for (int i = 0; i < na; ++i) {
                    Attribute attr = material.get(i);
                    if (attr instanceof TextureAttribute) {
                        TextureAttribute tattr = (TextureAttribute)attr;
                        add(tattr.textureDescription.texture);
                    }
                }
            }
        }
    }

    public static void remove(Model m) {
        // 遍历 Model 的所有 Node 来找到 NodePart
        for (Node node : m.nodes) {
            for (NodePart nodePart : node.parts) {
                Material material = nodePart.material;
                int na = material.size();
                for (int i = 0; i < na; ++i) {
                    Attribute attr = material.get(i);
                    if (attr instanceof TextureAttribute) {
                        TextureAttribute tattr = (TextureAttribute)attr;
                        remove(tattr.textureDescription.texture);
                    }
                }
            }
        }
    }
	
	public static void updateFilter() {
		for (Texture t : textures) {
			if (ConfigHelper.turnOnModelTextureLinearFilter())
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			else
				t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}
	}
}

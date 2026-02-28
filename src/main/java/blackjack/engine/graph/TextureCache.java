package blackjack.engine.graph;

import java.util.*;
import blackjack.engine.Consts;

public class TextureCache {


    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
        textureMap.put(Consts.DEFAULT_TEXTURE, new Texture(Consts.DEFAULT_TEXTURE));
    }

    //free resources
    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    public Texture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(texturePath, Texture::new);
    }

    public Texture getTexture(String texturePath) {
        Texture texture = null;
        if (texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if (texture == null) {
            texture = textureMap.get(Consts.DEFAULT_TEXTURE);
        }
        return texture;
    }
}
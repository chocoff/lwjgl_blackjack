//Load vertices data into GPU so it can be used for render, it receives an array of floats which represents a structured coord system for drawing a figure

package blackjack.engine.graph;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import blackjack.engine.Consts;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

/*
 * Vertex Buffer Object (VBO) is a mem buffer stored in the GPU's memory that stores vertices
 * Vertex Array Object (VAO) is an object that contains one or more VBOs which are usually called attribute lists
 * Each attribute can hold one type of data (pos, color, texture, etc)
 */

public class Mesh {

    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;
    private Vector3f aabbMax;
    private Vector3f aabbMin;

    public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textCoords, int[] indices) {
        this(positions, normals, tangents, bitangents, textCoords, indices,
                new int[Consts.MAX_WEIGHTS * positions.length / 3], new float[Consts.MAX_WEIGHTS * positions.length / 3],
                new Vector3f(), new Vector3f());
    }

    public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textCoords, int[] indices,
                int[] boneIndices, float[] weights, Vector3f aabbMin, Vector3f aabbMax) {
        
        this.aabbMin = aabbMin;
        this.aabbMax = aabbMax;

        numVertices = indices.length;
        vboIdList = new ArrayList<>();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Positions VBO (index 0 for attribs)
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(0, positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Normals VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer normalsBuffer = MemoryUtil.memCallocFloat(normals.length);
        normalsBuffer.put(0, normals);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
       
        // Tangents VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer tangentsBuffer = MemoryUtil.memCallocFloat(tangents.length);
        tangentsBuffer.put(0, tangents);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        // Bitangents VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer bitangentsBuffer = MemoryUtil.memCallocFloat(bitangents.length);
        bitangentsBuffer.put(0, bitangents);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, bitangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
       
        //Texture coordinates VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length);
        textCoordsBuffer.put(0, textCoords);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);

        //Bone weights
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer weightsBuffer = MemoryUtil.memCallocFloat(weights.length);
        weightsBuffer.put(weights).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);

        //Bone indices
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer boneIndicesBuffer = MemoryUtil.memCallocInt(boneIndices.length);
        boneIndicesBuffer.put(boneIndices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, 0, 0);

        //Index VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        //AFTER ALL VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //free mem resources
        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(normalsBuffer);
        MemoryUtil.memFree(tangentsBuffer);
        MemoryUtil.memFree(bitangentsBuffer);
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(weightsBuffer);
        MemoryUtil.memFree(boneIndicesBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    //free resources
    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);
    }

    //getters
    public int getNumVertices() {
        return numVertices;
    }

    public final int getVaoId() {
        return vaoId;
    }

    public Vector3f getAabbMax() {
        return aabbMax;
    }

    public Vector3f getAabbMin() {
        return aabbMin;
    }
}
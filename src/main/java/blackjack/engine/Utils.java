package blackjack.engine;

import java.io.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.List;

public class Utils {

    private Utils() {
    }

    public static String readFile(String filePath) {
      try (InputStream is = Utils.class.getResourceAsStream(filePath)) {
	if (is == null) {
	  throw new RuntimeException("Could not find resource: " + filePath);
	}
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
	  return reader.lines().collect(Collectors.joining("\n"));
	}
      } catch (IOException e) {
	throw new RuntimeException("Error reading resource file [" + filePath + "]", e);
      }
    }

    public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
      ByteBuffer buffer;
      try (InputStream source = Utils.class.getResourceAsStream(resource)) {
	  if (source == null) throw new FileNotFoundException(resource);
	  byte[] bytes = source.readAllBytes();
	  buffer = org.lwjgl.BufferUtils.createByteBuffer(bytes.length);
	  buffer.put(bytes);
	  buffer.flip();
      }
      return buffer;
    }
}

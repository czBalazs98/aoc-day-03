package hu.czmarkob;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws URISyntaxException {
		EngineSchematicResolver engineSchematicResolver = new EngineSchematicResolver(
				Paths.get(ClassLoader.getSystemResource("input.txt").toURI()));
		char[][] matrix = engineSchematicResolver.createMatrix();
		int rows = matrix.length;
		int cols = matrix[0].length;
		engineSchematicResolver.resolve(matrix, rows, cols);
		engineSchematicResolver.resolveGearRatios(matrix, rows, cols);
	}
}
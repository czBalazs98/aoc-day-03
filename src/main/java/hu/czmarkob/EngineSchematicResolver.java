package hu.czmarkob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EngineSchematicResolver {

	private final Path inputPath;

	public void resolve() {
		try {
			List<String> lines = Files.readAllLines(inputPath);
			int rows = lines.size();
			int cols = lines.get(0).length();
			char[][] matrix = new char[rows][cols];
			for ( int i = 0; i < rows; ++i ) {
				for ( int j = 0; j < cols; ++j ) {
					matrix[i][j] = lines.get(i).charAt(j);
				}
			}

			int sum = 0;
			for ( int i = 0; i < rows; ++i ) {
				for ( int j = 0; j < cols; ++j ) {
					StringBuilder numberString = new StringBuilder();
					List<Integer> indexes = new ArrayList<>();
					while (j < cols && getNumber(matrix[i][j]) != null) {
						numberString.append(getNumber(matrix[i][j]));
						indexes.add(j);
						++j;
					}

					if ( !numberString.toString().isBlank() ) {
						int number = Integer.parseInt(numberString.toString());
						boolean hasSymbol;
						for ( int k = 0; k < indexes.size(); ++k ) {
							if ( k == 0 ) {
								hasSymbol = indexes.size() == 1 ? checkFirstDigit(matrix, rows, cols, i, indexes.get(k)) ||
										checkLastDigit(matrix, rows, cols, i, indexes.get(k)) : checkFirstDigit(matrix, rows, cols, i, indexes.get(k));
							} else if ( k == indexes.size() - 1 ) {
								hasSymbol = checkLastDigit(matrix, rows, cols, i, indexes.get(k));
							} else {
								hasSymbol = checkMiddleDigits(matrix, rows, cols, i, indexes.get(k));
							}

							if ( hasSymbol ) {
								sum += number;
								break;
							}
						}
					}
				}
			}
			System.out.println(sum);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean checkMiddleDigits(char[][] matrix, int rows, int cols, int i, int j) {
		return (isInsideMatrix(rows, cols, i + 1, j) && isSymbol(matrix[i + 1][j])) ||
				(isInsideMatrix(rows, cols, i - 1, j) && isSymbol(matrix[i - 1][j]));
	}

	private boolean checkFirstDigit(char[][] matrix, int rows, int cols, int i, int j) {
		return checkMiddleDigits(matrix, rows, cols, i, j) || (isInsideMatrix(rows, cols, i, j - 1) && isSymbol(matrix[i][j - 1])) ||
				(isInsideMatrix(rows, cols, i - 1, j - 1) && isSymbol(matrix[i - 1][j - 1])) ||
				(isInsideMatrix(rows, cols, i + 1, j - 1) && isSymbol(matrix[i + 1][j - 1]));
	}

	private boolean checkLastDigit(char[][] matrix, int rows, int cols, int i, int j) {
		return checkMiddleDigits(matrix, rows, cols, i, j) || (isInsideMatrix(rows, cols, i, j + 1) && isSymbol(matrix[i][j + 1])) ||
				(isInsideMatrix(rows, cols, i - 1, j + 1) && isSymbol(matrix[i - 1][j + 1])) ||
				(isInsideMatrix(rows, cols, i + 1, j + 1) && isSymbol(matrix[i + 1][j + 1]));
	}


	private boolean isInsideMatrix(int rows, int cols, int i, int j) {
		return i >= 0 && i < rows && j >= 0 && j < cols;
	}

	private boolean isSymbol(char character) {
		return character != '.' && getNumber(character) == null;
	}

	private Integer getNumber(char character) {
		try {
			return Integer.parseInt(String.valueOf(character));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

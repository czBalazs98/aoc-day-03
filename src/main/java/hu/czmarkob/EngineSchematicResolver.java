package hu.czmarkob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EngineSchematicResolver {

	private int seq = 0;

	private final Path inputPath;

	public char[][] createMatrix() {
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
			return matrix;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void resolve(char[][] matrix, int rows, int cols) {
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
						}
					}
				}
			}
		}
		System.out.println(sum);
	}

	public void resolveGearRatios(char[][] matrix, int rows, int cols) {
		List<EngineNumber> engineNumbers = new ArrayList<>();
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
					EngineNumber engineNumber = new EngineNumber();
					engineNumber.setId(seq);
					seq++;
					engineNumber.setNum(number);
					for ( int k = 0; k < indexes.size(); ++k ) {
						if ( k == 0 ) {
							checkFirstDigitForStar(matrix, rows, cols, i, indexes.get(k), engineNumber);
						}

						if ( k == indexes.size() - 1 ) {
							checkLastDigitsForStar(matrix, rows, cols, i, indexes.get(k), engineNumber);
						}

						if ( k != 0 && k != indexes.size() - 1 ) {
							checkMiddleDigitsForStar(matrix, rows, cols, i, indexes.get(k), engineNumber);
						}
					}
					if ( !engineNumber.getStarCoordinates().isEmpty() ) {
						engineNumbers.add(engineNumber);
					}
				}
			}
		}

		int sum = 0;
		Map<Integer, Integer> checkedMap = new HashMap<>();
		for ( int i = 0; i < engineNumbers.size(); ++i ) {
			for ( EngineNumber engineNumber : engineNumbers ) {
				Integer mapValue1 = checkedMap.get(engineNumber.getId());
				Integer mapValue2 = checkedMap.get(engineNumbers.get(i).getId());
				if ( engineNumbers.get(i).getId() != engineNumber.getId() &&
						!engineNumbers.get(i).getStarCoordinates().stream()
									  .filter(num -> engineNumber.getStarCoordinates().contains(num)).toList().isEmpty() &&
						(mapValue1 == null || mapValue1 != engineNumbers.get(i).getId()) &&
						(mapValue2 == null || mapValue2 != engineNumber.getId()) ) {
					sum += engineNumbers.get(i).getNum() * engineNumber.getNum();
					checkedMap.put(engineNumbers.get(i).getId(), engineNumber.getId());
				}
			}
		}
		System.out.println(sum);

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

	private void checkFirstDigitForStar(char[][] matrix, int rows, int cols, int i, int j, EngineNumber engineNumber) {
		checkMiddleDigitsForStar(matrix, rows, cols, i, j, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i, j - 1, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i - 1, j - 1, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i + 1, j - 1, engineNumber);
	}

	private void checkMiddleDigitsForStar(char[][] matrix, int rows, int cols, int i, int j, EngineNumber engineNumber) {
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i + 1, j, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i - 1, j, engineNumber);
	}

	private void checkLastDigitsForStar(char[][] matrix, int rows, int cols, int i, int j, EngineNumber engineNumber) {
		if ( engineNumber.getNum() >= 10 ) {
			checkMiddleDigitsForStar(matrix, rows, cols, i, j, engineNumber);
		}
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i, j + 1, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i - 1, j + 1, engineNumber);
		setStarCoordinatesForEngineNumber(matrix, rows, cols, i + 1, j + 1, engineNumber);
	}

	private void setStarCoordinatesForEngineNumber(char[][] matrix, int rows, int cols, int i, int j, EngineNumber engineNumber) {
		if ( isInsideMatrix(rows, cols, i, j) && matrix[i][j] == '*' ) {
			engineNumber.getStarCoordinates().add(new Coordinates(i, j));
		}
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

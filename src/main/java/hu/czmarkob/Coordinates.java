package hu.czmarkob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Coordinates {
	private int rowNum;
	private int colNum;

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Coordinates coordinates)) {
			return false;
		}

		return coordinates.rowNum == rowNum && coordinates.colNum == colNum;
	}
}

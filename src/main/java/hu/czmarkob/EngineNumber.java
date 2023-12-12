package hu.czmarkob;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EngineNumber {

	private int id;

	private int num;

	private List<Coordinates> starCoordinates = new ArrayList<>();
}

package indi.atlantis.framework.jobhub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;

/**
 * 
 * RunningState
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public enum RunningState implements EnumConstant {

	FAILED(0, "Failed"), COMPLETED(1, "Completed"), SKIPPED(2, "Skipped"), FINISHED(3, "Finished"), RUNNING(99, "Running");

	private final int value;
	private final String repr;

	private RunningState(int value, String repr) {
		this.value = value;
		this.repr = repr;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

	public String getRepr() {
		return repr;
	}

	@JsonCreator
	public static RunningState valueOf(int value) {
		for (RunningState state : RunningState.values()) {
			if (state.getValue() == value) {
				return state;
			}
		}
		throw new IllegalArgumentException("Unknown value: " + value);
	}

}

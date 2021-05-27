package indi.atlantis.framework.chaconne;

/**
 * 
 * StatType
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public enum StatType {

	BY_YEAR("DATE_FORMAT(execute_time,'%Y') AS execution", "execution"), BY_MONTH("DATE_FORMAT(execute_time,'%Y-%m') AS execution",
			"execution"), BY_DATE("DATE_FORMAT(execute_time,'%Y-%m-%d') AS execution", "execution");

	private final String extraColumns;
	private final String extraGroupingColumns;

	private StatType(String extraColumns, String extraGroupingColumns) {
		this.extraColumns = extraColumns;
		this.extraGroupingColumns = extraGroupingColumns;
	}

	public String getExtraColumns() {
		return extraColumns;
	}

	public String getExtraGroupingColumns() {
		return extraGroupingColumns;
	}

}

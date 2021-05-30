package indi.atlantis.framework.chaconne.console;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Result
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class Result<T> {
	
	private boolean success;
	private String msg;
	private T data;
	private String errorMsg;
	private String[] errorDetail;
	private long elapsed;
	private String requestPath;
	
	public Result() {
	}
	
	public static <T> Result<T> success(T data){
		return success(data, "ok");
	}

	public static <T> Result<T> success(T data, String msg) {
		Result<T> model = new Result<T>();
		model.setSuccess(true);
		model.setMsg(msg);
		model.setData(data);
		return model;
	}

	public static <T> Result<T> failure(String msg) {
		Result<T> model = new Result<T>();
		model.setSuccess(false);
		model.setMsg(msg);
		return model;
	}

}

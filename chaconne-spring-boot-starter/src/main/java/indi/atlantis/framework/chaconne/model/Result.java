/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

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
public class Result<T> implements Serializable {

	private static final long serialVersionUID = -6257798137365527003L;

	private boolean success;
	private String msg;
	private T data;

	public Result() {
	}
	
	public static <T> Result<T> success(T data){
		return success(data, "ok");
	}

	public static <T> Result<T> success(T data, String msg) {
		Result<T> result = new Result<T>();
		result.setSuccess(true);
		result.setMsg(msg);
		result.setData(data);
		return result;
	}

	public static <T> Result<T> failure(String msg) {
		Result<T> result = new Result<T>();
		result.setSuccess(false);
		result.setMsg(msg);
		return result;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

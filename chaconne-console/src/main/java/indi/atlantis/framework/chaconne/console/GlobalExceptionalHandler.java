/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.console;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.paganini2008.devtools.ExceptionUtils;

import indi.atlantis.framework.chaconne.console.utils.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * GlobalExceptionalHandler
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionalHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Result<?>> handleException(HttpServletRequest request, Exception e) throws Exception {
		log.error(e.getMessage(), e);
		Result<?> result = Result.failure("Internal Server Error");
		result.setRequestPath(request.getServletPath());
		result.setErrorMsg(e.getMessage());
		result.setErrorDetail(ExceptionUtils.toArray(e));
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			result.setElapsed(System.currentTimeMillis() - sign);
		}
		return ResponseEntity.ok(result);
	}

}
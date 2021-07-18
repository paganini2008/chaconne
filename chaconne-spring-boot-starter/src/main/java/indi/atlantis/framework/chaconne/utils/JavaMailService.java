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
package indi.atlantis.framework.chaconne.utils;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.MailContentSource;
import indi.atlantis.framework.chaconne.RunningState;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JavaMailService
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class JavaMailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MailContentSource contentSource;

	@Value("${atlantis.framework.chaconne.mail.username}")
	private String defaultMailSender;

	@Value("${atlantis.framework.chaconne.mail.default.subject:Job Email Warning}")
	private String defaultMailSubject;

	@Value("${atlantis.framework.chaconne.mail.default.recipients:}")
	private String defaultRecipients;

	public void sendMail(String to, long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState,
			Throwable reason) {
		if (contentSource.isHtml()) {
			sendHtmlMail(to, traceId, jobKey, attachment, startDate, runningState, reason);
		} else {
			sendSimpleMail(to, traceId, jobKey, attachment, startDate, runningState, reason);
		}
	}

	public void sendSimpleMail(String to, long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState,
			Throwable reason) {
		try {
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(defaultMailSender);
			simpleMailMessage.setTo(to.split(","));
			simpleMailMessage.setSubject(defaultMailSubject);
			if (StringUtils.isNotBlank(defaultRecipients)) {
				simpleMailMessage.setCc(defaultRecipients.split(","));
			}
			simpleMailMessage.setText(contentSource.getContent(traceId, jobKey, attachment, startDate, runningState, reason));
			javaMailSender.send(simpleMailMessage);
			log.info("Send mail to: {}", to);
		} catch (Exception e) {
			log.error("Failed to send text mail.", e);
		}
	}

	public void sendHtmlMail(String to, long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState,
			Throwable reason) {
		try {
			MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
			mimeMessageHelper.setFrom(defaultMailSender);
			mimeMessageHelper.setTo(to.split(","));
			mimeMessageHelper.setSubject(defaultMailSubject);
			if (StringUtils.isNotBlank(defaultRecipients)) {
				mimeMessageHelper.setCc(defaultRecipients.split(","));
			}
			mimeMessageHelper.setText(contentSource.getContent(traceId, jobKey, attachment, startDate, runningState, reason), true);
			javaMailSender.send(mimeMailMessage);
			log.info("Send mail to: {}", to);
		} catch (Exception e) {
			log.error("Failed to send html mail.", e);
		}
	}

}

package indi.atlantis.framework.jobhub.utils;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.MailContentSource;
import indi.atlantis.framework.jobhub.RunningState;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JavaMailService
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JavaMailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MailContentSource contentSource;

	@Value("${jobsoup.mail.username}")
	private String defaultMailSender;

	@Value("${jobsoup.mail.default.subject:JobSoup Email Warning}")
	private String defaultMailSubject;

	@Value("${jobsoup.mail.default.recipients:}")
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

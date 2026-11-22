package com.trackorjargh.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.trackorjargh.javaclass.User;

@Service
public class MailComponent {
	@Autowired
	private JavaMailSender emailSender;
	
	private String to;
	private String subject;
	private String message;
		
	class SendSimpleEmail extends Thread {
	    public void run() {
	    		sendSimpleMimeMessage(to, subject, message);
	    }
	}

	public void sendSimpleMessage(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);

			emailSender.send(message);
		} catch (MailException exception) {
			exception.printStackTrace();
		}
	}

	public void sendSimpleMimeMessage(String to, String subject, String text) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, true);

			emailSender.send(message);
		} catch (MessagingException exception) {
			exception.printStackTrace();
		}
	}

	public void sendVerificationEmail(User user, String urlPage) {
		this.to = user.getEmail();
		this.subject = "Verificación del usuario " + user.getName() + " en TrackOrJargh";
		this.message = "<body><p>Por favor pinche en este <a href='" + urlPage
				+ "'>enlace</a> para poder usar todas las ventajas de TrackOrJack</p></body>";
		
		Thread sendEmail = new Thread( new SendSimpleEmail());
		sendEmail.start();
	}

	public void sendChangePassEmail(User user, String urlPage) {
		this.to = user.getEmail();
		this.subject = "Cambio de contraseña TrackOrJargh";
		this.message = "<body><p>Ha solicitado un cambio de contraseña, por favor pinche en este <a href='" + urlPage
				+ "'>enlace</a> para cambiar su contraseña</p></body>";

		Thread sendEmail = new Thread( new SendSimpleEmail());
		sendEmail.start();
	}
}

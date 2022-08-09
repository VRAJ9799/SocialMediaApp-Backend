package com.vraj.socialmediaapp.helpers;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.models.commons.EmailModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class EmailHelper {

    private final SendGrid _sendgrid;

    public EmailHelper(SendGrid sendgrid) {
        _sendgrid = sendgrid;
    }

    public void sendEmail(EmailModel emailModel) {
        try {
            Mail mail = new Mail();
            Personalization personalization = new Personalization();
            mail.setFrom(new Email("vrajshah363@gmail.com", "Vraj Shah"));
            emailModel.getTos().forEach(to -> {
                personalization.addTo(new Email(to));
            });
            emailModel.getBccs().forEach(bcc -> {
                personalization.addBcc(new Email(bcc));
            });
            if (emailModel.isTemplateMail()) {
                mail.setTemplateId(emailModel.getTemplateId());
                emailModel.getValues().forEach((key, value) -> {
                    personalization.addDynamicTemplateData(key, value);
                });
            } else {
                mail.setSubject(emailModel.getSubject());
                Content content = new Content("text/plain", emailModel.getBody());
                mail.addContent(content);
            }
            mail.addPersonalization(personalization);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = _sendgrid.api(request);
            if (response.getStatusCode() != HttpStatus.ACCEPTED.value()) {
                throw new StatusException("Error while sending mail.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception exception) {
            throw new StatusException("Error while sending mail.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

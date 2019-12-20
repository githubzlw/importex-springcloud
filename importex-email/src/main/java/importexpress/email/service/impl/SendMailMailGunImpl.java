package importexpress.email.service.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import importexpress.common.pojo.mail.MailBean;
import importexpress.email.service.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.MediaType;

import static sun.security.x509.X509CertInfo.SUBJECT;

/**
 * @author luohao
 * @date 2018/10/23
 */
@Slf4j
@Deprecated
@Service
public class SendMailMailGunImpl implements SendMail {

    private final static String MAILGUN_DOMAIN_NAME = "mg.import-express.com";
    private final static String MAILGUN_API_KEY = "key-5af11fc491becf8970b5c8eb45bbf6af";
    private final static String MAIL_GUN_ADDRESS = "Import-Express.com<admin@importx.com>";
    private static HostnameVerifier hv = (urlHostName, session) -> {
        log.info("Warning: URL Host: " + urlHostName + " vs. "
                + session.getPeerHost());
        return true;
    };


    @Override
    public void sendMail(MailBean mailBean) throws IllegalStateException {
        try {
            trustAllHttpsCertificates();
        } catch (Exception e) {
            log.error("sendMail",e);
            throw new IllegalStateException(e.getMessage());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", MAILGUN_API_KEY));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN_NAME + "/messages");
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("from", MAIL_GUN_ADDRESS);
        formData.field("to", mailBean.getTo());
        if (StringUtils.isNotBlank(mailBean.getBcc())) {
            formData.field("bcc", mailBean.getBcc());
        }
        formData.field("subject", SUBJECT);
        formData.field("html", mailBean.getBody());
        int res = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, formData).getStatus();
        if (res != 200) {
            log.error("Send email faild:" + SUBJECT);
            throw new RuntimeException("Send email faild");
        }
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}

package com.lemon.framework.util.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author zhanghai
 * create on 2018/11/8
 */
@SuppressWarnings("unused")
public class MailUtils {

    /**
     * 发送邮件
     *
     * @param host     smtp服务地址
     * @param port     地址端口
     * @param ssl      是否ssl
     * @param user     邮件服务号
     * @param password 授权码或密码
     * @param from     发送人
     * @param to       接收人
     * @param subject  标题
     * @param content  内容
     * @param files    附件文件
     * @throws MessagingException MessagingException
     */
    public static void sendMail(
            String host,
            int port,
            boolean ssl,
            String user,
            String password,
            String from,
            String to,
            String subject,
            String content,
            File... files) throws MessagingException {

        Properties prop = new Properties();
        prop.setProperty("mail.host", host);
        prop.setProperty("mail.smtp.port", "" + port);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.quitwait", "false");
        prop.setProperty("mail.smtp.ssl.enable", "" + ssl);

        if (ssl) {
            prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            prop.setProperty("mail.smtp.socketFactory.fallback", "false");
            prop.setProperty("mail.smtp.socketFactory.port", "" + port);
            prop.setProperty("mail.smtp.localhost", "127.0.0.1");
        }

        //1、创建session
        Session session;
//        if (ssl) {
        session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
//        } else {
//            session = Session.getInstance(prop);
//        }
        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        session.setDebug(false);

//        Transport ts = null;
//        try {
        //2、通过session得到transport对象
//            ts = session.getTransport();
        //3、连上邮件服务器
//            if (!ssl) {
//                ts.connect(host, port, user, password);
//            }
        //4、创建邮件
        Message message = createAttachMail(
                session,
                from,
                to,
                subject,
                content,
                files);
        //5、发送邮件
        Transport.send(message);
//            ts.sendMessage(message, message.getAllRecipients());
//        } finally {
//            if (ts != null)
//                ts.close();
//        }
    }

    private static MimeMessage createAttachMail(Session session, String from, String to, String subject, String content, File... files)
            throws MessagingException {

        MimeMessage message = new MimeMessage(session);

        // 设置邮件的基本信息
        // 发件人
        message.setFrom(new InternetAddress(from));
        // 收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        // 邮件标题，字符集保证中文无乱码
        message.setSubject(subject, "UTF-8");

        //创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(content, "text/html;charset=UTF-8");

        //创建容器描述数据关系
        MimeMultipart mp = new MimeMultipart("mixed");
        mp.addBodyPart(text);

        //创建邮件附件
        for (File file : files) {
            MimeBodyPart attach = new MimeBodyPart();

            DataHandler dh = new DataHandler(new FileDataSource(file));
            attach.setDataHandler(dh);

            // 解决附件文件名中文乱码问题
            try {
                attach.setFileName(MimeUtility.encodeText(dh.getName()));
            } catch (UnsupportedEncodingException e) {
                attach.setFileName(dh.getName());
            }

            mp.addBodyPart(attach);
        }

        message.setContent(mp);
        message.saveChanges();

        //将创建的Email写入到E盘存储
//        message.writeTo(new FileOutputStream("E:\\attachMail.eml"));
        //返回生成的邮件
        return message;
    }
}

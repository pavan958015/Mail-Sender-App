import java.awt.*;
import java.io.File;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;

public class Home extends JFrame {

    JTextField t1, t3, t4, t6;
    JPasswordField t2;
    JTextArea t5;
    File f;

    public Home() {

        setTitle("Mail Sender");
        setSize(600, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {}

        setContentPane(new GradientPanel());
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 450));
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        t1 = new JTextField();
        t2 = new JPasswordField();
        t3 = new JTextField();
        t4 = new JTextField();

        // Message Area FIXED
        t5 = new JTextArea();
        t5.setLineWrap(true);
        t5.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(t5);
        scrollPane.setPreferredSize(new Dimension(300, 120));

        t6 = new JTextField();

        JButton sendBtn = new JButton("Send Mail");
        JButton attachBtn = new JButton("Attach File");
        JButton sendAttachBtn = new JButton("Send with Attachment");

        styleButton(sendBtn);
        styleButton(sendAttachBtn);

        JLabel title = new JLabel("Mail Sender", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("From Email:"), gbc);
        gbc.gridx = 1;
        panel.add(t1, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(t2, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("To Email:"), gbc);
        gbc.gridx = 1;
        panel.add(t3, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        panel.add(t4, gbc);

        // Message FIX with proper expand
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Message:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy++;
        panel.add(attachBtn, gbc);
        gbc.gridx = 1;
        panel.add(t6, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(sendBtn, gbc);
        gbc.gridx = 1;
        panel.add(sendAttachBtn, gbc);

        add(panel);

        // Actions
        attachBtn.addActionListener(e -> {
            JFileChooser j = new JFileChooser();
            j.showOpenDialog(null);
            f = j.getSelectedFile();
            if (f != null) t6.setText(f.getAbsolutePath());
        });

        sendBtn.addActionListener(e -> sendMail(false));
        sendAttachBtn.addActionListener(e -> sendMail(true));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void sendMail(boolean withAttachment) {

        try {
            String from = t1.getText();
            String password = new String(t2.getPassword());
            String subject = t4.getText();
            String message = t5.getText();

            String[] emails = t3.getText().split(",");
            InternetAddress[] addresses = new InternetAddress[emails.length];

            for (int i = 0; i < emails.length; i++) {

                String email = emails[i].trim();

                //  validation
                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(this, "Invalid Email: " + email);
                    return; // stop sending
                }

                addresses[i] = new InternetAddress(email);
            }

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "465");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.ssl.enable", "true");

            Session session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipients(Message.RecipientType.TO, addresses);
            msg.setSubject(subject);

            if (withAttachment && f != null) {

                MimeMultipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(message);

                MimeBodyPart filePart = new MimeBodyPart();
                filePart.attachFile(f);

                multipart.addBodyPart(textPart);
                multipart.addBodyPart(filePart);

                msg.setContent(multipart);

            } else {
                msg.setText(message);
            }

            Transport.send(msg);

            JOptionPane.showMessageDialog(this, "Emails sent successfully:");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Home().setVisible(true);
    }
}
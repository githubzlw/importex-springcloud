package com.importexpress.cart.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author jack.luo
 * @create 2020/6/12 17:06
 * Description
 */
@Service
public class SFtpUtil {


    private final Config config;

    public SFtpUtil(Config config) {
        this.config = config;
    }

    private SSHClient setupSshj() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(config.SFTP_REMOTEHOST);
        client.authPassword(config.SFTP_USERNAME, config.SFTP_PASSWORD);
        return client;
    }

    /**
     * 通过sftp上传文件到远程服务器
     * @param fileName
     * @throws IOException
     */
    public void uploadFile(String fileName) throws IOException {
        SSHClient sshClient = setupSshj();
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.put(config.SAVE_CART_PATH+fileName, config.SFTP_SAVE_CART_PATH + fileName);

        sftpClient.close();
        sshClient.disconnect();
    }
}

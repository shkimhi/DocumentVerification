package com.sh.documentverification.services;

import com.jcraft.jsch.*;
import com.sh.documentverification.dao.SftpMapper;
import com.sh.documentverification.dto.Result;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SftpService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SftpMapper sftpMapper;
    private final LedgerService ledgerService;

    public Session session = null;
    public Channel channel = null;
    public ChannelSftp channelSftp = null;
    private static final Path remoteFilePath = Path.of("/home/web/Downloads");

    public void sftpInit() throws Exception {
        String connIp = "172.16.5.14";        //접속 SFTP 서버 IP
        int connPort = 22;    //접속 PORT
        String connId = "web";        //접속 ID
        String connPw = "123qwe";        //접속 PW
        int timeout = 10000;    //타임아웃 10초

        JSch jsch = new JSch();
        try {
            //세션객체 생성
            session = jsch.getSession(connId, connIp, connPort);
            session.setPassword(connPw); //password 설정

            //세션관련 설정정보 설정
            java.util.Properties config = new java.util.Properties();

            //호스트 정보 검사하지 않는다.
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(timeout); //타임아웃 설정

            //log.info("connect.. " + connIp);
            System.out.println("connect.. " + connIp);
            session.connect();    //접속

            channel = session.openChannel("sftp");    //sftp 채널 접속
            channel.connect();

        } catch (JSchException e) {
            //log.error(e);
            logger.error(String.valueOf(e));
            System.out.println(e);
            throw e;
        }
        channelSftp = (ChannelSftp) channel;
    }

    /**
     * SFTP 서버 접속 종료
     */
    public void disconnect() {
        if (channelSftp != null) {
            channelSftp.quit();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    public void sftpFileUpload(InputStream localPath, String uploadFileNm, String fileHash) throws Exception {
        try {
            sftpInit();
            //파일을 가져와서 inputStream에 넣고 저장경로를 찾아 업로드
            channelSftp.cd(String.valueOf(remoteFilePath));
            channelSftp.put(localPath, uploadFileNm, new SftpProgressMonitor() {
                long FileSize = 0;
                long SendFileSize = 0;
                int per = 0;

                @Override
                public void init(int i, String s, String s1, long l) {
                    this.FileSize = l;
                }

                @Override
                public boolean count(long l) {
                    this.SendFileSize += l;
                    long p = this.FileSize * 100 / this.FileSize;
                    if (p > per) {

                        //System.out.print("=");

                        System.out.println(per + "%");
                        per++;
                    }
                    return true;
                }

                @Override
                public void end() {
                }
            });
            String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            JSONObject params = new JSONObject();
            params.put("uploadFileNm", uploadFileNm);
            params.put("authenticatedUser", userId);
            params.put("remoteFilePath", String.valueOf(remoteFilePath));
            params.put("hashValue", fileHash);

            sftpMapper.insertFile(params);

            //log.info("sftpFileUpload success.. ");
            System.out.println("sftpFileUpload success.. ");
        } catch (SftpException se) {
            //log.error(se);
            logger.error(String.valueOf(se));
            System.out.println(se);
            throw se;
        } catch (Exception e) {
            //log.error(e)
            logger.error(String.valueOf(e));
            System.out.println(e);
            throw e;
        } finally {
            try {
                localPath.close();
            } catch (IOException ioe) {
                //log.error(ioe);
                logger.error(String.valueOf(ioe));
                System.out.println(ioe);
            }
        }
    }

    /**
     * SFTP 서버 파일 다운로드
     *
     * @param downloadPath
     * @param localFilePath
     */
    public void sftpFileDownload(String downloadPath, String localFilePath) throws Exception {
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;

        try {
            sftpInit();
            //SFTP 서버 파일 다운로드 경로
            String cdDir = downloadPath.substring(0, downloadPath.lastIndexOf("/") + 1);
            //파일명
            String fileName = downloadPath.substring(downloadPath.lastIndexOf("/") + 1, downloadPath.length());

            channelSftp.cd(cdDir);

            File file = new File(downloadPath);
            bis = new BufferedInputStream(channelSftp.get(fileName));

            //파일 다운로드 SFTP 서버 -> 다운로드 서버
            File newFile = new File(localFilePath + fileName);
            os = new FileOutputStream(newFile);
            bos = new BufferedOutputStream(os);

            int readCount;

            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }

            //log.debug("sftpFileDownload success.. ");
            System.out.println("sftpFileDownload success.. ");
        } catch (Exception e) {
            //log.error(e);
            logger.error(String.valueOf(e));
            System.out.println(e);
            throw e;
        } finally {
            try {
                bis.close();
                bos.close();
                os.close();
            } catch (IOException e) {
                //log.error(e);
                logger.error(String.valueOf(e));
                System.out.println(e);
            }
        }
    }

    public static String getHash(InputStream path) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        FileInputStream fileInputStream = (FileInputStream) path;

        byte[] dataBytes = new byte[1024];

        Integer nRead = 0;
        while ((nRead = fileInputStream.read(dataBytes)) != -1) {
            messageDigest.update(dataBytes, 0, nRead);
        }

        byte[] mdBytes = messageDigest.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for (Integer i = 0; i < mdBytes.length; i++) {
            stringBuffer.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)).substring(1);
        }
        return stringBuffer.toString();
    }

}

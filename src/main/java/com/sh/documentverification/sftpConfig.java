package com.sh.documentverification;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
public class sftpConfig {
    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost("172.16.5.14");
        factory.setPort(22);
        factory.setUser("web");
        factory.setPassword("123qwe");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }
}

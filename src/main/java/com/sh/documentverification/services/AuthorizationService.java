package com.sh.documentverification.services;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Set;

@Service
public class AuthorizationService {

    public final static String NETWORK_CONFIG_PATH = "../Documents/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.yaml";
    private final static String PEM_FILE_PATH = "../Documents/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
    private final static String ORG1_CA_URL = "https://172.19.0.2:7054";
    private final static String ADMIN_USER = "admin";

    public final String getUserId() throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return id;
    }

    public String enrollAdmin() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, InvalidArgumentException, CertificateException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        HFCAClient caClient = getCaClient();
        Wallet wallet = getWallet(); 

        boolean adminExists = wallet.get(ADMIN_USER) != null;
        if (adminExists) {
            return "관리자 아이디(\"" + ADMIN_USER + "\")가 이미 지갑에 존재 합니다.";
        }
        Enrollment enrollment = getAdminEnrollment(caClient);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment); 
        wallet.put(ADMIN_USER, user); 
        return "관리자 아이디(\"" + ADMIN_USER + "\")를 성공적으로 등록하고 지갑에 추가했습니다. ";
    }


    public String registerUser() throws Exception {
        HFCAClient caClient = getCaClient();
        Wallet wallet = getWallet();

        boolean userExists = wallet.get(getUserId()) != null;

        if (userExists) {
            return "사용자 아이디(\"" + getUserId() + "\")가 이미 지갑에 존재 합니다.";
        }

        boolean adminExists = wallet.get(ADMIN_USER) != null;
        if (!adminExists) {
            return "관리자(\"" + ADMIN_USER + "\") 를 먼저 등록하고 지갑에 추가해야 합니다.";
        }

        Identity adminIdentity = wallet.get(ADMIN_USER);
        User admin = getNewUser(getAdminEnrollment(caClient), adminIdentity.getMspId());
        RegistrationRequest registrationRequest = new RegistrationRequest(getUserId());
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(getUserId());
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll(getUserId(), enrollmentSecret);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put(getUserId(), user);

        return "사용자 아이디(\"" + getUserId() + "\")를 성공적으로 등록하고 지갑에 추가했습니다.";
    }

    //CA와 상호작용 하기 위한 CaClient 생성
    private HFCAClient getCaClient() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CryptoException, InvalidArgumentException {
        Properties props = new Properties();
        props.put("pemFile", PEM_FILE_PATH); 
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(ORG1_CA_URL, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        return caClient;
    }

    // id 관리를 위한 지갑 생성
    public Wallet getWallet() throws IOException {
        return Wallets.newFileSystemWallet(Paths.get("wallet"));
    }

    //어드민 인증서 발급.
    private Enrollment getAdminEnrollment(HFCAClient caClient) throws EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        return caClient.enroll(ADMIN_USER, "adminpw", enrollmentRequestTLS);
    }

    private User getNewUser(Enrollment enrollment, String mspId) {
        return new User() {
            @Override
            public String getName() {
                return ADMIN_USER;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return "org1.department1";
            }

            @Override
            public Enrollment getEnrollment() {
                return enrollment;
            }

            @Override
            public String getMspId() {
                return mspId;
            }

        };
    }
}
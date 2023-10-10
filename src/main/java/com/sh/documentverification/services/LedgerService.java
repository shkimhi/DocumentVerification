package com.sh.documentverification.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.documentverification.dto.File;
import com.sh.documentverification.dto.Result;
import org.hyperledger.fabric.gateway.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.sh.documentverification.services.AuthorizationService.NETWORK_CONFIG_PATH;

@Service
public class LedgerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private  Gateway.Builder builder;
    private  Wallet wallet;
    private  Path networkConfigPath;

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    public LedgerService(AuthorizationService authorizationService) throws IOException{
        wallet = authorizationService.getWallet();
        networkConfigPath = Paths.get(NETWORK_CONFIG_PATH);
        builder = Gateway.createBuilder();
    }

    public String createFile(Result filehash) throws IOException {
        builder.identity(wallet, authorizationService.getUserId()).networkConfig(networkConfigPath).discovery(true);
        try (Gateway gateway = builder.connect()){
            byte[] result = getContract(gateway).submitTransaction("createHashFile",
                    filehash.getKey(),
                    filehash.getRecord().getFilehash(),
                    filehash.getRecord().getFiledate(),
                    filehash.getRecord().getFilename(),
                    filehash.getRecord().getUsername());
            return new String(result, StandardCharsets.UTF_8);

        } catch (ContractException | InterruptedException | TimeoutException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
    public List<Result> queryFile(String key) throws IOException, ContractException {
        builder.identity(wallet, authorizationService.getUserId()).networkConfig(networkConfigPath).discovery(true);
        try(Gateway gateway = builder.connect()) {
            byte[] resultByte = getContract(gateway).evaluateTransaction("queryFile", key);
            System.out.println(Arrays.toString(resultByte));
            String resultString = new String (resultByte, StandardCharsets.UTF_8);

            System.out.println(resultString+":resultString");
            List<Result> results = deserializeResultList(resultString);
            System.out.println(results);
            return results;
        }
    }

    public List<Result> queryAllHashFile() throws ContractException, IOException{
        builder.identity(wallet, authorizationService.getUserId()).networkConfig(networkConfigPath).discovery(true);
        try(Gateway gateway = builder.connect()) {

            byte[] resultBytes = getContract(gateway).evaluateTransaction("queryAllHashFile");

            String resultString = new String(resultBytes, StandardCharsets.UTF_8);


            List<Result> results = deserializeResultList(resultString);

            return results;

        }
    }
    public List<Result> queryUserid(String username) throws ContractException,IOException {
        builder.identity(wallet, authorizationService.getUserId()).networkConfig(networkConfigPath).discovery(true);
        try (Gateway gateway = builder.connect()) {

            byte[] resultByte = getContract(gateway).evaluateTransaction("queryUser", username);
            String resultString = new String(resultByte, StandardCharsets.UTF_8);

            List<Result> results = deserializeResultList(resultString);
            System.out.println(results);
            return results;

        }
    }
    private Contract getContract(Gateway gateway){
        Network network = gateway.getNetwork("mychannel");
        return network.getContract("fabcar");
    }
    private List<Result> deserializeResultList(String result) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Result>> typeRef = new TypeReference<List<Result>>() {};
        return mapper.readValue((result), typeRef);
    }

}

package com.sh.documentverification.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.documentverification.dto.Result;
import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.sh.documentverification.services.AuthorizationService.NETWORK_CONFIG_PATH;

@Service
public class LedgerService {

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

        } catch (ContractException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Result> queryFile(String key) throws IOException, ContractException {
        builder.identity(wallet, authorizationService.getUserId()).networkConfig(networkConfigPath).discovery(true);
        try(Gateway gateway = builder.connect()) {
            byte[] resultByte = getContract(gateway).evaluateTransaction("queryFile", key);
            String resultString = new String (resultByte, StandardCharsets.UTF_8);

            List<Result> results = deserializeResultList(resultString);
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
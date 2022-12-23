package de.neozo.jblockchain.node.service;


import de.neozo.jblockchain.common.SignatureUtils;
import de.neozo.jblockchain.common.domain.Address;
import de.neozo.jblockchain.common.domain.Node;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.common.domain.TransactionOutput;
import de.neozo.jblockchain.node.dto.TransactionDTO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;


@Service
public class TransactionService {

    private final static Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final AddressService addressService;

    /**
     * Pool of Transactions which are not included in a Block yet.
     */
    private Set<Transaction> transactionPool = new HashSet<>();

    @Autowired
    public TransactionService(AddressService addressService) {
        this.addressService = addressService;
    }


    public Set<Transaction> getTransactionPool() {
        return transactionPool;
    }

    /**
     * Add a new Transaction to the pool
     * @param transaction Transaction to add
     * @return true if verifcation succeeds and Transaction was added
     */
    public synchronized boolean add(Transaction transaction) {
        if (verify(transaction)) {
            transactionPool.add(transaction);
            return true;
        }
        return false;
    }

    /**
     * Remove Transaction from pool
     * @param transaction Transaction to remove
     */
    public void remove(Transaction transaction) {
        transactionPool.remove(transaction);
    }

    /**
     * Does the pool contain all given Transactions?
     * @param transactions Collection of Transactions to check
     * @return true if all Transactions are member of the pool
     */
    public boolean containsAll(Collection<Transaction> transactions) {
        return transactionPool.containsAll(transactions);
    }
    public void createCoinBase() throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
    	Transaction coinbaseTx = new Transaction();
    		KeyPair keyPair = SignatureUtils.generateKeyPair();
            Files.write(Paths.get("key.priv"), keyPair.getPrivate().getEncoded());
            Files.write(Paths.get("key.pub"), keyPair.getPublic().getEncoded());
            Files.writeString(Paths.get("keypub.txt"),Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
            Files.writeString(Paths.get("keypriv.txt"),Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
            Address address = new Address("Master Address", Files.readAllBytes(Paths.get("key.pub")));
            LOG.info("First address "+Base64.encodeBase64String(address.getHash()));
            addressService.add(address);
    	coinbaseTx.createCoinBase(address.getPublicKey());
    	this.transactionPool.add(coinbaseTx);
    }
    public Transaction createTransaction(TransactionDTO transactionDTO,List<TransactionOutput> UTXOs) throws Exception {
    	Address sender = addressService.getByHash(transactionDTO.getSenderHash());
    	Address receiver = addressService.getByHash(transactionDTO.getReceiverHash());
    	List<TransactionOutput> spentableOutputs = getSpentableOutputs(sender.getPublicKey(), UTXOs, transactionDTO.getValue());
    	if(spentableOutputs == null) {
    		throw new Exception("Not enough amount!");
    	}
    	Transaction newTx = new Transaction(sender.getPublicKey(),receiver.getPublicKey(),transactionDTO.getValue());
    	byte[] signature = SignatureUtils.sign(newTx.DataString().getBytes(),transactionDTO.getPrivateKey());
    	newTx.setSignature(signature);
    	newTx.setHashID(newTx.calculateHash());
    	newTx.processTransaction(spentableOutputs);
    	return newTx;
    }
    public List<TransactionOutput> getSpentableOutputs(byte[] senderHash,List<TransactionOutput> UTXOs,float value){
    	float accumulated = 0f;
    	List<TransactionOutput> unpentOutputs = new ArrayList<>();
    	for (TransactionOutput transactionOutput : UTXOs) {
    		if(accumulated >= value) {
    			break;
    		}
			if(!Arrays.equals(senderHash,transactionOutput.getReciepient())) {
				continue;
			}
			accumulated += transactionOutput.getValue();
			unpentOutputs.add(transactionOutput);
			
		}
    	if(accumulated < value) {
    		return null;
    	}
    	return unpentOutputs;
    }
    private boolean verify(Transaction transaction) {
        try {
            if (!SignatureUtils.verify(transaction.DataString().getBytes(), transaction.getSignature(), transaction.getSenderHash())) {
                LOG.warn("Invalid signature");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Error while verification", e);
            return false;
        }

        // correct hash
        if (!Arrays.equals(transaction.getHashID(),transaction.calculateHash())) {
            LOG.warn("Invalid hash");
            return false;
        }

        return true;
    }

    /**
     * Download Transactions from other Node and them to the pool
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    public void retrieveTransactions(Node node, RestTemplate restTemplate) {
        Transaction[] transactions = restTemplate.getForObject(node.getAddress() + "/transaction", Transaction[].class);
        Collections.addAll(transactionPool, transactions);
        LOG.info("Retrieved " + transactions.length + " transactions from node " + node.getAddress());
    }
}

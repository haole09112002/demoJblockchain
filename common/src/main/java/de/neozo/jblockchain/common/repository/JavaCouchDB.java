package de.neozo.jblockchain.common.repository;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import de.neozo.jblockchain.common.domain.Address;
import de.neozo.jblockchain.common.domain.Block;

import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.common.domain.TransactionInput;
import de.neozo.jblockchain.common.domain.TransactionOutput;

public class JavaCouchDB {
	private static CouchDbConnector db;
	private volatile static JavaCouchDB instance;
	
	public static JavaCouchDB getInstance() throws MalformedURLException {
		if(instance == null) {
			synchronized (JavaCouchDB.class) {
				if(instance == null) {
					instance = new JavaCouchDB();
				}
			}
		}
		return instance;
	}
	private JavaCouchDB() throws MalformedURLException {
		initConnect();
	}
	private void initConnect() throws MalformedURLException {
		 HttpClient httpClient = new StdHttpClient.Builder()  
				.url("http://localhost:5984").username("Admin").password("13122002a").build();
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient); 
		 db = dbInstance.createConnector("Block",true);
	}
	public void addBlock(Block block) {
		Map<String,Object> map = new HashMap<>();
		if(db.find(Map.class,Integer.toString(block.getIndex())) != null) {
			return;
		}
		map.put("_id", Integer.toString(block.getIndex()));
		map.put("hash",block.getHash());
		map.put("previousBlockHash",block.getPreviousBlockHash());
		map.put("transactions", block.getTransactions());
		map.put("merkleRoot",block.getMerkleRoot());
		map.put("tries",block.getTries());
		map.put("timestamp",block.getTimestamp());
		db.create(map);
	}
	@SuppressWarnings("unchecked")
	public Block getBlock(String index) {
		Block block = new Block();
		Map<String,Object> map = new HashMap<>();
		map = db.find(Map.class,index);
		block.setIndex(Integer.parseInt((String)map.get("_id")));
		block.setHash(Base64.decodeBase64((String) map.get("hash")));
		block.setPreviousBlockHash(Base64.decodeBase64((String) map.get("previousBlockHash")));
		List<Map<String,Object>> mapTransactions = (List<Map<String,Object>>) map.get("transactions");
		block.setTransactions(getTransactions(mapTransactions));
		block.setMerkleRoot(Base64.decodeBase64((String) map.get("merkleRoot")));
		block.setTries(Integer.toUnsignedLong((int)map.get("tries")));
		block.setTimestamp((long)map.get("timestamp"));
		return block;
	}
	public List<Transaction> getTransactions(List<Map<String,Object>> mapTransactions){
		List<Transaction> transactions = new ArrayList<>();
		for (Map<String, Object> map : mapTransactions) {
			Transaction tempTransaction = new Transaction() ;
			tempTransaction.setHashID(Base64.decodeBase64((String) map.get("hashID")));
			tempTransaction.setSenderHash(Base64.decodeBase64((String) map.get("senderHash")));
			tempTransaction.setReceiverHash(Base64.decodeBase64((String) map.get("receiverHash")));
			tempTransaction.setValue(Float.parseFloat(Double.toString((double)map.get("value"))));
			tempTransaction.setSignature(Base64.decodeBase64((String) map.get("signature")));
			tempTransaction.setTimestamp((long)map.get("timestamp"));
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> mapTxinputs = (List<Map<String,Object>>) map.get("txInputs");
			for (Map<String, Object> mapInput: mapTxinputs) {
				TransactionInput txInput = new TransactionInput();
				txInput.setTransactionOutputId((String) mapInput.get("transactionOutputId"));
				@SuppressWarnings("unchecked")
				Map<String,Object> mapTxOutput  = (Map<String, Object>) mapInput.get("UTXO");
				TransactionOutput txOutput= new TransactionOutput();
				txOutput.setId((String) mapTxOutput.get("id"));
				txOutput.setReciepient(Base64.decodeBase64((String) mapTxOutput.get("reciepient")));
				txOutput.setValue(Float.parseFloat(Double.toString((double)mapTxOutput.get("value"))) );
				txOutput.setParentTransactionId(Base64.decodeBase64((String) mapTxOutput.get("parentTransactionId")));
				txInput.setUTXO(txOutput);
				tempTransaction.getTxInputs().add(txInput);
			}
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> mapTxOutputs = (List<Map<String,Object>>) map.get("txOutputs");
			for(Map<String,Object> mapOutput : mapTxOutputs) {
				TransactionOutput tempOutput = new TransactionOutput();
				tempOutput.setId((String) mapOutput.get("id"));
				tempOutput.setReciepient(Base64.decodeBase64((String) mapOutput.get("reciepient")));
				tempOutput.setValue(Float.parseFloat(Double.toString((double)mapOutput.get("value"))) );
				tempOutput.setParentTransactionId(Base64.decodeBase64((String) mapOutput.get("parentTransactionId")));
				tempTransaction.getTxOutputs().add(tempOutput);
			}
			transactions.add(tempTransaction);
		}
		return transactions;
	}
	public List<Block> getAllBlock(){
		List<Block> blocks = new ArrayList<>();
		List<String> listBlockHash= new ArrayList<>();
		listBlockHash = db.getAllDocIds();
		for(String idString : listBlockHash) {
			blocks.add(getBlock(idString));
		}
		return blocks;
	}
}

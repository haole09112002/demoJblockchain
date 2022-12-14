package de.neozo.jblockchain.common.repository;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;



import de.neozo.jblockchain.common.domain.Block;

import de.neozo.jblockchain.common.domain.Transaction;

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
				.url("http://localhost:5984").username("hao").password("123456").build();
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
		List<Transaction> transactions = new ArrayList<>();
		for(Map<String,Object> map2 : mapTransactions) {
			Transaction tempTransaction = new Transaction();
			tempTransaction.setHash(Base64.decodeBase64((String) map2.get("hash")));
			tempTransaction.setSenderHash(Base64.decodeBase64((String) map2.get("senderHash")));
			tempTransaction.setSignature(Base64.decodeBase64((String) map2.get("signature")));
			tempTransaction.setText((String) map2.get("text"));
			tempTransaction.setTimestamp((long) map2.get("timestamp"));
			transactions.add(tempTransaction);
		}
		block.setTransactions(transactions);
		block.setMerkleRoot(Base64.decodeBase64((String) map.get("merkleRoot")));
		block.setTries(Integer.toUnsignedLong((int)map.get("tries")));
		block.setTimestamp((long)map.get("timestamp"));
		return block;
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

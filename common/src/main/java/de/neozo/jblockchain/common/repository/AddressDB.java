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

import de.neozo.jblockchain.common.domain.Address;

public class AddressDB {
	private static CouchDbConnector db;
	private volatile static AddressDB instance;
	
	public static AddressDB getInstance() throws MalformedURLException {
		if(instance == null) {
			synchronized (JavaCouchDB.class) {
				if(instance == null) {
					instance = new AddressDB();
				}
			}
		}
		return instance;
	}
	private AddressDB() throws MalformedURLException {
		initConnect();
	}
	private void initConnect() throws MalformedURLException {
		 HttpClient httpClient = new StdHttpClient.Builder()  
				.url("http://localhost:5984").username("hao").password("123456").build();
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient); 
		 db = dbInstance.createConnector("Address",true);
	}
	public void addAddress(Address address) {
		Map<String,Object> map = new HashMap<>();
		if(db.find(Map.class, Base64.encodeBase64String(address.getHash())) != null) {
			return;
		}
		map.put("_id",Base64.encodeBase64String(address.getHash()));
		map.put("name", address.getName());
		map.put("publickey",Base64.encodeBase64String(address.getPublicKey()));
		db.create(map);
	}
	@SuppressWarnings("unchecked")
	public Address getAddress(String hash) {
		Address address = new Address();
		Map<String,Object> map = new HashMap<>();
		map = db.find(Map.class, hash);
		address.setHash(Base64.decodeBase64((String) map.get("_id")));
		address.setName((String) map.get("name"));
		address.setPublicKey(Base64.decodeBase64((String) map.get("publickey")));
		return address;
	}
	public List<Address> getAllAddresses(){
		List<Address> addresses = new ArrayList<>();
		List<String> listAddressesHash = new ArrayList<>();
		listAddressesHash = db.getAllDocIds();
		System.out.println("size="+listAddressesHash.size());
		for (String addressString : listAddressesHash) {
			addresses.add(getAddress(addressString));
		}
		return addresses;
	}
	
}

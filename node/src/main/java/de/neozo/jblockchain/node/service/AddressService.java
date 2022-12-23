package de.neozo.jblockchain.node.service;


import de.neozo.jblockchain.common.SignatureUtils;
import de.neozo.jblockchain.common.domain.Address;
import de.neozo.jblockchain.common.domain.Node;
import de.neozo.jblockchain.common.repository.AddressDB;
import de.neozo.jblockchain.node.dto.PeerDTO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AddressService {

    private final static Logger LOG = LoggerFactory.getLogger(AddressService.class);

    /**
     * Mapping of Address hash -> Address object
     */
    private Map<String, Address> addresses = new HashMap<>();

    /**
     * Get a specific Address
     * @param hash hash of Address
     * @return Matching Address for hash
     */
    public Address getByHash(byte[] hash) {
        return addresses.get(Base64.encodeBase64String(hash));
    }

    /**
     * Return all Addresses from map
     * @return Collection of Addresses
     */
    public Collection<Address> getAll() {
        return addresses.values();
    }
    public boolean isEmpty() {
    	return addresses.isEmpty();
    }

    /**
     * Add a new Address to the map
     * @param address Address to add
     */
    public synchronized void add(Address address) {
        addresses.put(Base64.encodeBase64String(address.getHash()), address);
        try {
			AddressDB.getInstance().addAddress(address);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Download Addresses from other Node and them to the map
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    public void retrieveAddresses(Node node, RestTemplate restTemplate) {
        Address[] addresses = restTemplate.getForObject(node.getAddress() + "/address", Address[].class);
        Arrays.asList(addresses).forEach(this::add);
        LOG.info("Retrieved " + addresses.length + " addresses from node " + node.getAddress());
    }
    public void loadLocalAddress() {
    	try {
    		List<Address> listAddresses = AddressDB.getInstance().getAllAddresses();
    		for (Address address : listAddresses) {
    			addresses.put(Base64.encodeBase64String(address.getHash()), address);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    public byte[] getHashByPublickey(byte[] publickey) {
    	for (Map.Entry m : addresses.entrySet()) {
			Address address = (Address) m.getValue();
			if(Arrays.equals(address.getPublicKey(),publickey)) {
				return address.getHash();
			}
		}
    	return null;
    }
    public KeyPair generateKeyPair() {
    	try {
    	       KeyPair keyPair = SignatureUtils.generateKeyPair();
    	       Files.write(Paths.get("key.priv"), keyPair.getPrivate().getEncoded());
    	       Files.write(Paths.get("key.pub"), keyPair.getPublic().getEncoded());
    	       return keyPair;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
 
    }
    public boolean verifyAccount(PeerDTO peerDTO) throws Exception 
    {
    	byte[] signature = SignatureUtils.sign(peerDTO.getMessage().getBytes(),peerDTO.getPrivateKey());
    		Address address = getByHash(peerDTO.getSenderHash());
    		LOG.info(address.getName());
    		if(address!= null)
    		{
    			Boolean check = SignatureUtils.verify(peerDTO.getMessage().getBytes(), signature, address.getPublicKey());
    			if(check)
    			{
    				LOG.info("Verify account senderHash: " + Base64.encodeBase64String(peerDTO.getSenderHash()));
    				return true;
    			}
    			else
    			{
    				LOG.info("Sai PV: " + Base64.encodeBase64String(peerDTO.getSenderHash()));
    				return false;
    			}
    		}
    		else 
    		{
    			throw new Exception("Sai Senderhash");
    		}
    }
    
    
}
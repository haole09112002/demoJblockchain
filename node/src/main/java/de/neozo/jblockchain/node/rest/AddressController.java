package de.neozo.jblockchain.node.rest;


import de.neozo.jblockchain.common.domain.Address;

import de.neozo.jblockchain.common.domain.Peer;
import de.neozo.jblockchain.node.dto.AddressDTO;
import de.neozo.jblockchain.node.service.AddressService;
import de.neozo.jblockchain.node.service.NodeService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

import java.security.KeyPair;
import java.util.Collection;


@RestController()
@RequestMapping("address")
public class AddressController {

    private final static Logger LOG = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private final NodeService nodeService;

    @Autowired
    public AddressController(AddressService addressService, NodeService nodeService) {
        this.addressService = addressService;
        this.nodeService = nodeService;
    }

    /**
     * Get all Addresses this node knows
     * @return JSON list of Addresses
     */
    @RequestMapping
    Collection<Address> getAdresses() {
        return addressService.getAll();
    }


    /**
     * Add a new Address
     * @param address the Address to add
     * @param publish if true, this Node is going to inform all other Nodes about the new Address
     * @param response Status Code 202 if Address was added, 406 if submitted hash is already present
     */
//    @PostMapping(value = "/add",consumes = "application/x-www-form-urlencoded")
//    void addAddress( Peer peer, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
//    	Address address = new Address(peer.getName(),peer.getPublicKey());
//        LOG.info("Add address " + address.getHash());
//        if (addressService.getByHash(address.getHash()) == null) {
//            addressService.add(address);
//
//            if (publish != null && publish) {
//                nodeService.broadcastPut("address", address);
//            }
//            response.setStatus(HttpServletResponse.SC_ACCEPTED);
//        } else {
//            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
//        }
//    }
    
    
    
    @PutMapping(consumes = "application/json")
    AddressDTO  addAddress(@RequestBody String name, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
    	KeyPair keyPair = addressService.generateKeyPair();
    	if(keyPair != null)
    	{
    		Address address = new Address(name, keyPair.getPublic().getEncoded());
        	LOG.info("Add address " + Base64.encodeBase64String(address.getHash()));
        	if (addressService.getByHash(address.getHash()) == null) {
                addressService.add(address);

                if (publish != null && publish) {
                    nodeService.broadcastPut("address/add", address);
                }
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                AddressDTO addressDTO = new AddressDTO(address.getHash(), address.getName(), address.getPublicKey(), keyPair.getPrivate().getEncoded());
                return addressDTO;
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return null;
            }
    	}
    	return null;
    }
    
    @PutMapping( value = "add", consumes = "application/json")
    void  addAddress(@RequestBody Address address, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {

    		
        	LOG.info("Add address " + Base64.encodeBase64String(address.getHash()));
        	if (addressService.getByHash(address.getHash()) == null) {
                addressService.add(address);

                if (publish != null && publish) {
                    nodeService.broadcastPut("address/add", address);
                }
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
    	}

    }
}

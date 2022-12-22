package de.neozo.jblockchain.node.rest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.neozo.jblockchain.common.SignatureUtils;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.node.dto.PeerDTO;
import de.neozo.jblockchain.node.dto.TransactionDTO;
import de.neozo.jblockchain.node.service.AddressService;
import de.neozo.jblockchain.node.service.TransactionService;



@RestController()
@RequestMapping("login")
public class ViewController {
	private final AddressService addressService;
	@Autowired
	public ViewController(AddressService addressService)
	{
		this.addressService = addressService;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public boolean checkLogin(@RequestBody PeerDTO tx, HttpServletResponse response)
	{
//		byte[] signature = SignatureUtils.sign(tx.getMessage().getBytes(), tx.getPrivateKey());
//        Transaction transaction = new Transaction(tx.getMessage(), tx.getSenderHash(), signature);
//		Transaction transaction = transactionService.createTransaction(transactionDTO, UTXOs);
		try {
			return addressService.verifyAccount(tx);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
		return false;
         
       
		
	}
	
	
	
}

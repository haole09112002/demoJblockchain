package de.neozo.jblockchain.node.rest;


import de.neozo.jblockchain.common.domain.Address;
import de.neozo.jblockchain.common.domain.Block;
import de.neozo.jblockchain.node.Config;
import de.neozo.jblockchain.node.service.AddressService;
import de.neozo.jblockchain.node.service.BlockService;
import de.neozo.jblockchain.node.service.MiningService;
import de.neozo.jblockchain.node.service.NodeService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("block")
public class BlockController {

    private static final Logger LOG = LoggerFactory.getLogger(BlockController.class);

    private final BlockService blockService;
    private final NodeService nodeService;
    private final MiningService miningService;
    private final AddressService addressService;

    @Autowired
    public BlockController(BlockService blockService, NodeService nodeService, MiningService miningService,AddressService addressService) {
        this.blockService = blockService;
        this.nodeService = nodeService;
        this.miningService = miningService;
        this.addressService = addressService;
    }

    /**
     * Retrieve all Blocks in order of mine date, also known as Blockchain
     * @return JSON list of Blocks
     */
    @RequestMapping
    List<Block> getBlockchain() {
    	
        return blockService.getBlockchain();
    }
    @RequestMapping(method = RequestMethod.PUT)
    void addNewBlock(@RequestBody Block block, @RequestParam(required = false) Boolean publish, HttpServletResponse response) throws MalformedURLException {
        boolean success = blockService.append(block,Config.NEW_BLOCK);
        if (success) {
        	 LOG.info("Add  newBlock " + Base64.encodeBase64String(block.getHash()));
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            miningService.stopMiner();
            LOG.info("Stopped miner");
            if (publish != null && publish) {
                nodeService.broadcastPut("block", block);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
    @RequestMapping(value="/oldblock",method = RequestMethod.PUT)
    void addBlock(@RequestBody Block block,@RequestParam("index") int index, HttpServletResponse response) throws MalformedURLException {
    	if(blockService.getLastBlock().getIndex() < index ) {
    		LOG.info("Add oldBlock with index ="+ index);
    		boolean success = blockService.append(block, Config.OLD_BLOCK);
    		if (success) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
    	}
    }
    @RequestMapping(value = "/version",method = RequestMethod.GET)
    int getVersionBlock() {
    	if(blockService.getLastBlock() != null) {
    		return blockService.getLastBlock().getIndex();
    	}
    	return 0;
    }
    @RequestMapping(method = RequestMethod.POST, value = "balance")
    public float getBlance(@RequestBody String senderHash,HttpServletResponse response) {
    	
    	Address address = addressService.getByHash(Base64.decodeBase64(senderHash));
    	
//    	Address address = addressService.getByHash(senderHash);
    	if(address != null)
    	{
    		LOG.info("Balance: " +blockService.getBalance(address.getPublicKey()) );
    		return blockService.getBalance(address.getPublicKey());
    	}
    	return -1;
    }
    @RequestMapping(value = "/getblocks", params = {"index"},method =  RequestMethod.GET)
    public List<Block> getMissingBlocks(@RequestParam("index") int index){
    	List<Block> missingBlocks = new ArrayList<>();
    	for(int i = index - 1 ; i < blockService.getBlockchain().size(); i++ ) {
    		missingBlocks.add(blockService.getBlockchain().get(i));
    	}
    	return missingBlocks;
    }
    
    @RequestMapping(path = "stop-miner")
    public void stopMiner() {
        miningService.stopMiner();
    }

}
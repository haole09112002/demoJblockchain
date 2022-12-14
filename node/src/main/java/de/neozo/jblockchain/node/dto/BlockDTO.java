package de.neozo.jblockchain.node.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.neozo.jblockchain.common.domain.Block;
import de.neozo.jblockchain.common.domain.Transaction;

public class BlockDTO  {
	  private String id;
      private String revision;
      private byte[] hash;
      private byte[] previousBlockHash;
  		private List<Transaction> transactions;
  		private byte[] merkleRoot;
  		private long tries;
  		private long timestamp;
      
      
      
      
      public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public byte[] getPreviousBlockHash() {
		return previousBlockHash;
	}

	public void setPreviousBlockHash(byte[] previousBlockHash) {
		this.previousBlockHash = previousBlockHash;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public byte[] getMerkleRoot() {
		return merkleRoot;
	}

	public void setMerkleRoot(byte[] merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	public long getTries() {
		return tries;
	}

	public void setTries(long tries) {
		this.tries = tries;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

      @JsonProperty("_id")
      public String getId() {
              return id;
      }

      @JsonProperty("_id")
      public void setId(String s) {
              id = s;
      }

      @JsonProperty("_rev")
      public String getRevision() {
              return revision;
      }

      @JsonProperty("_rev")
      public void setRevision(String s) {
              revision = s;
      }
      public BlockDTO() {
		
	}
	
      
}

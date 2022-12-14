package de.neozo.jblockchain.node.dto;

public class TransactionDTO {

    private String message;


    private byte[] senderHash;


    private byte[] privateKey;

    public TransactionDTO() {
		// TODO Auto-generated constructor stub
	}

	public TransactionDTO(String text, byte[] senderHash, byte[] privateKey) {
		this.message = text;
		this.senderHash = senderHash;
		this.privateKey = privateKey;

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getSenderHash() {
		return senderHash;
	}

	public void setSenderHash(byte[] senderHash) {
		this.senderHash = senderHash;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	
    
}

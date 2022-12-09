package de.neozo.jblockchain.common.domain;

public class Peer {
    private String name;
    private String publicKey;
    public Peer() {
		// TODO Auto-generated constructor stub
	}
    
	public Peer(String name, String publicKey) {
		this.name = name;
		this.publicKey = publicKey;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}

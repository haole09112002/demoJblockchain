package de.neozo.jblockchain.common.domain;

public class Peer {
    private String name;
    private byte[] publicKey;
    public Peer() {
		// TODO Auto-generated constructor stub
	}
    
	public Peer(String name, byte[]  publicKey) {
		this.name = name;
		this.publicKey = publicKey;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[]  getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(byte[]  publicKey) {
		this.publicKey = publicKey;
	}
}

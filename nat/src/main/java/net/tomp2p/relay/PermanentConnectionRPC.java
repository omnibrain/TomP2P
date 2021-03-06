package net.tomp2p.relay;

import net.tomp2p.connection.PeerConnection;
import net.tomp2p.connection.Responder;
import net.tomp2p.futures.FuturePeerConnection;
import net.tomp2p.message.Message;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DirectDataRPC;

/**
 * RPC to set up a permanent connection to a relay peer.
 * 
 * @author Raphael Voellmy
 * 
 */
//TODO: can this be combined with RelayRPC? Is there a need for having this separate?
public class PermanentConnectionRPC extends DirectDataRPC {
	
	private FuturePeerConnection futurePeerConnection = null;
	private final Peer peer;
	private final PeerAddress unreachablePeer;
	private final DirectDataRPC originalDirectDataRPC;
	
	/**
     * @param peer
     *            Peer to establish a permanent connection to
     * @param unreachablePeer
     *            PeerAddress of the unreachable peer
     */
	public PermanentConnectionRPC(Peer peer, PeerAddress unreachablePeer, int bla) {
		super(peer.getPeerBean(), peer.getConnectionBean());
		this.peer = peer;
		this.unreachablePeer = unreachablePeer;
		this.originalDirectDataRPC = peer.getDirectDataRPC();
	}
	
    @Override
    public void handleResponse(final Message message, PeerConnection peerConnection, final boolean sign, Responder responder) throws Exception {

    	if(message.getSender().equals(unreachablePeer) && futurePeerConnection == null) {
    		futurePeerConnection = new FuturePeerConnection(message.getSender());
            futurePeerConnection.setDone(peerConnection);
            //new RelayForwarder(futurePeerConnection, peer);
            
            // "restore" original direct data RPC
            peer.setDirectDataRPC(originalDirectDataRPC);
            
            peer.getPeerBean().peerMap().peerFound(message.getSender(), message.getSender());
    	}
        super.handleResponse(message, peerConnection, sign, responder);
    }

}

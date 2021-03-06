package net.tomp2p.relay;

import net.tomp2p.futures.BaseFutureImpl;
import net.tomp2p.futures.FuturePeerConnection;
import net.tomp2p.peers.PeerAddress;

/**
 * PeerConnection can be retrieved from this future after it has successfully
 * been established
 * 
 * @author Raphael Voellmy
 * 
 */
public class RelayConnectionFuture extends BaseFutureImpl<RelayConnectionFuture> {

    final private PeerAddress relayAddress;
    private FuturePeerConnection futurePeerConnection = null;

    public RelayConnectionFuture(final PeerAddress relayAddress) {
        this.relayAddress = relayAddress;
        self(this);
    }

    public PeerAddress relayAddress() {
        return relayAddress;
    }

	public RelayConnectionFuture futurePeerConnection(final FuturePeerConnection futurePeerConnection) {
		synchronized (lock) {
			if (!setCompletedAndNotify()) {
				return this;
			}
			this.futurePeerConnection = futurePeerConnection;
			if (futurePeerConnection == null) {
	            setFailed("FuturePeerConnection is null");
	        } else if (!futurePeerConnection.getObject().channelFuture().channel().isActive()) {
	            setFailed("Peer connection is not active");
	        } else if (!futurePeerConnection.getObject().channelFuture().channel().isOpen()) {
	            setFailed("Peer connection is not open");
	        } else {
	            this.futurePeerConnection = futurePeerConnection;
	            type = FutureType.OK;
	        }
		}
		notifyListeners();
		return this;
	}

    public FuturePeerConnection futurePeerConnection() {
        synchronized (lock) {
            return futurePeerConnection;
        }
    }
}

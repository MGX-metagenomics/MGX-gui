
package de.cebitec.mgx.api.access.datatransfer;

/**
 *
 * @author sjaenick
 */
public abstract class DownloadBaseI extends TransferBaseI {
    
    private CallbackI cb = null;

    protected void abortTransfer(String reason, long total) {
        setErrorMessage(reason);
        fireTaskChange(TransferBaseI.NUM_ELEMENTS_TRANSFERRED, total);
        fireTaskChange(TransferBaseI.TRANSFER_FAILED, 1);
    }

    public void setProgressCallback(CallbackI cb) {
        this.cb = cb;
    }

    protected CallbackI getProgressCallback() {
        return cb != null ? cb
                : new DownloadBaseI.NullCallBack();
    }

    public abstract boolean download();

    public abstract long getProgress();

    protected final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }
}

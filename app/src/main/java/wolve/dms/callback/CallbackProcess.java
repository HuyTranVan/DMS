package wolve.dms.callback;

public interface CallbackProcess {
    void onStart();
    void onError();
    void onSuccess(String name);
}

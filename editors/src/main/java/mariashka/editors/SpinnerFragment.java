package mariashka.editors;

import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by mariashka on 1/16/15.
 */

public class SpinnerFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<List<PhotoItem>>{

    private ProgressDialog progressDialog;

    private final MemoryLoader loader;
    private PhotoLoadListener taskLoaderListener;

    protected void setLoadListener(PhotoLoadListener taskLoaderListener){
        this.taskLoaderListener = taskLoaderListener;
    }

    protected void onLoadComplete(final List<PhotoItem> data) {
        if(taskLoaderListener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    taskLoaderListener.onLoadFinished(data);
                }
            });
        }
    }

    protected void onCancelLoad(final List<PhotoItem> list) {
        if (taskLoaderListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    taskLoaderListener.onCancelLoad(list);
                }
            });
        }
    }

    protected SpinnerFragment(MemoryLoader loader){
        loader.setHandler(handler);
        this.loader = loader;

        Bundle args = new Bundle();
        args.putInt("message", 0);
        setArguments(args);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        LoaderManager lm = getLoaderManager();
        if (lm.getLoader(0) != null) {
            lm.initLoader(0, loader.getArguments(), this);
        }else{
            startLoading();
        }
    }

    @Override
    public ProgressDialog onCreateDialog(Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading data from memory");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    protected void startLoading() {
        getLoaderManager().initLoader(0, loader.getArguments(), this);
    }

    @Override
    public Loader<List<PhotoItem>> onCreateLoader(int id, Bundle args) {
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<PhotoItem>> loader, List<PhotoItem> data) {
        onLoadComplete(data);
        ((MemoryLoader) loader).setHandler(null);

        hideDialog();
    }

    @Override
    public void onLoaderReset(Loader<List<PhotoItem>> loader) {}

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        List<PhotoItem> items = loader.getList();
        loader.cancelLoad();
        loader.setCanceled(true);

        onCancelLoad(items);
    }

    private void hideDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        });
    }

    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
        }
    };
}

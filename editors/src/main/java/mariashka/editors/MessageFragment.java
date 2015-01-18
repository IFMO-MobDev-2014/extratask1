package mariashka.editors;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by mariashka on 1/16/15.
 */

public class MessageFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<List<PhotoItem>>, DialogInterface.OnCancelListener {

    private ProgressDialog progressDialog;

    private final PhotoLoader loader;
    private final int maxProgress;
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

    protected MessageFragment(PhotoLoader loader){
        loader.setHandler(handler);
        this.loader = loader;
        this.maxProgress = 100;

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
        progressDialog.setMessage("Loading started. Please wait or cancel.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        progressDialog.setProgress(0);
        progressDialog.setOnCancelListener(this);
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
        ((PhotoLoader) loader).setHandler(null);

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
            Integer progress = PhotoLoader.getProgress(msg);
            if (progress != null){
                progressDialog.setProgress(progress);
            }
        }
    };
}


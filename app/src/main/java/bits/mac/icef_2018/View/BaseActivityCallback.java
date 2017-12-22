package bits.mac.icef_2018.View;

import android.content.Context;

public interface BaseActivityCallback {

    void showProgressDialog(String message, Context ctx);

    void hideProgressDialog();

    void setToolbarTitle(String title);
}
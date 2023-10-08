package space.syncore.cockpit.soundeffect.soundeffectactivity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


/**
 * @author n027087
 */
public class AdigoSoundDialogFragment extends DialogFragment {

    private static final String TAG = "Dialog_intentFragment";
    private static final String INIT_EXTERNAL_DATA = "Application_Sound";
    private static final int VERSION_CONFIGURATION_LITE = 66;
    private static final int VERSION_CONFIGURATION_PRO = 77;
    private static final int START_UNDER_NORMAL_STATE = 88;
    private static final int OPEN_AMBIENT_SOUND_START = 99;
    private static final int WINDOW_TYPE = 2508;
    private static final float AMBIGUITY_LEVEL = 0.6f;
    private static final String VERSION_CONFIGURATION_PRO_LITE = "pro_lite";
    private static final int INITIAL_VALUE = 0;


    private static final int INDEX_TWO = 2;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_ZERO = 0;

    private Window window;
    private TabLayout tablayout;
    private String[] titles;
    private List<Fragment> fragments = new ArrayList<>();
    private View view;
    private AdigoSoundActivity activity;
    private ImageView ivTittle;
    private TextView tvTittle;
    private int mProLiteData;
    private Button sureBtn;
    private Button cancelBtn;

    public AdigoSoundDialogFragment() {

    }

    public AdigoSoundDialogFragment(AdigoSoundActivity activity) {
        this.activity = activity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setWindowAttrs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_layout, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialog);
        L.dd();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.dd();
        sureBtn = view.findViewById(R.id.sure_button);
        cancelBtn = view.findViewById(R.id.cancel_button);
        sureBtn.setOnClickListener(v -> {
            dismiss();
        });
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });
        setWindowAttrs();
    }

    @Override
    public void onStart() {
        super.onStart();
        L.dd();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //触发白天黑夜模式回调，存入状态true
        activity.finish();
        startActivity(new Intent(activity, AdigoSoundActivity.class));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        L.dd();
    }

    private void setWindowAttrs(){
        window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_sound_bg);
        window.getAttributes().width = Tools.getScreenW(getContext());
        window.getAttributes().height = Tools.getScreenH(getContext());
        window.setType(WINDOW_TYPE);
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(AMBIGUITY_LEVEL);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        fix();

        transparentNavBar(window);
    }

    private void fix() {
//        window.setLayout(Tools.getScreenW(getContext()), Tools.getScreenH(getContext()));
//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        );
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    public static void transparentNavBar(@NonNull final Window window) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            L.dd("目前只针对AH8,版本");
//            WindowInsetsController controller = window.getInsetsController();
//            controller.show(WindowInsets.Type.statusBars());
//        } else {
            View decorView = window.getDecorView();
            int vis = decorView.getSystemUiVisibility();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(vis | option);
//        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        L.d(TAG, "AdigoSoundDialog onDismiss");
        if (activity != null) {
            activity.finish();
        }
    }
}

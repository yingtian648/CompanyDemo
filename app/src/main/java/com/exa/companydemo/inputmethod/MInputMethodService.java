package com.exa.companydemo.inputmethod;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;

import com.exa.baselib.utils.L;
import com.exa.companydemo.R;

import static android.view.inputmethod.EditorInfo.IME_ACTION_GO;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEND;

public class MInputMethodService extends InputMethodService implements View.OnClickListener {
    private EditorInfo mEditorInfo;
    private InputConnection mInput;
    private int mImeOptions;
    private long startTime = 0;
    private BaseInputConnection mBaseInputConnection;
    private int mCursor = 0;
    private final int ids[] = {
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
            R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0,
            R.id.btnEnter, R.id.btnDel
    };
    private TextView btnEnter, btnDel;
    private View inputView;
    private PointF startPoint;
    private boolean deling = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public View onCreateInputView() {//当IME第一次显示，系统会调用 onCreateInputView() 回调。在你实现的这个方法中，你创建你想要的IME窗口的布局并返回给系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getWindow().setDecorFitsSystemWindows(true);
        }
        inputView = getLayoutInflater().inflate(R.layout.input_layout, null);
        initView();
        return inputView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        btnEnter = inputView.findViewById(R.id.btnEnter);
        btnDel = inputView.findViewById(R.id.btnDel);
        for (int i = 0; i < ids.length; i++) {
            inputView.findViewById(ids[i]).setOnClickListener(this);
        }
        btnDel.setOnTouchListener((v, event) -> {
            L.d("event.getAction():" + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startPoint = new PointF(event.getX(), event.getY());
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (deling) {
                        mInput.deleteSurroundingText(1, 0);
                    }
                    if (System.currentTimeMillis() - startTime > 400) {
                        deling = true;
                        startTime = System.currentTimeMillis();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    deling = false;
                    break;
            }
            return false;
        });
    }

    @Override
    public View onCreateCandidatesView() {//候选视图是供用户选择可选词和推荐词的视图。在IME的生命周期中，系统在准备好显示备选视图
        return super.onCreateCandidatesView();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {//当输入字段接收焦点并且您的IME启动时，系统调用 onStartInputView()
        mEditorInfo = info;
        mInput = getCurrentInputConnection();
        setImeOptionText();
        L.d("onStartInputView inputType:" + getInputType(info.inputType));
        super.onStartInputView(info, restarting);
    }

    private void setImeOptionText() {
        mImeOptions = mEditorInfo.imeOptions;
        switch (mImeOptions) {
            case IME_ACTION_SEND:
                btnEnter.setText("发送");
                break;
            case IME_ACTION_GO:
                btnEnter.setText("Go");
                break;
            case IME_ACTION_NEXT:
                btnEnter.setText("下一步");
                break;
            case IME_ACTION_SEARCH:
                btnEnter.setText("搜索");
                break;
        }
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                mInput.commitText("0", 1);
                break;
            case R.id.btn1:
                mInput.commitText("1", 1);
                break;
            case R.id.btn2:
                mInput.commitText("2", 1);
                break;
            case R.id.btn3:
                mInput.commitText("3", 1);
                break;
            case R.id.btn4:
                mInput.commitText("4", 1);
                break;
            case R.id.btn5:
                mInput.commitText("5", 1);
                break;
            case R.id.btn6:
                mInput.commitText("6", 1);
                break;
            case R.id.btn7:
                mInput.commitText("7", 1);
                break;
            case R.id.btn8:
                mInput.commitText("8", 1);
                break;
            case R.id.btn9:
                mInput.commitText("9", 1);
                break;
            case R.id.btnEnter:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    mInput.closeConnection();
                }
                break;
            case R.id.btnDel:
                mInput.deleteSurroundingText(1, 0);
                break;
            default:
                break;
        }
    }

    private String getInputType(int inputType) {
        switch (inputType) {
            case InputType.TYPE_CLASS_TEXT:
                return "TYPE_CLASS_TEXT";
            case InputType.TYPE_CLASS_NUMBER:
                return "TYPE_CLASS_NUMBER";
            case InputType.TYPE_CLASS_PHONE:
                return "TYPE_CLASS_PHONE";
            case InputType.TYPE_TEXT_VARIATION_PASSWORD://密码
                return "TYPE_TEXT_VARIATION_PASSWORD";
            default:
                return "OTHERS";
        }
    }
}

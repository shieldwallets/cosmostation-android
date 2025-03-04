package wannabit.io.cosmostaion.fragment.chains.kava;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import kava.swap.v1beta1.QueryOuterClass;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.kava.DepositPoolActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.task.gRpcTask.KavaSwapPoolInfoGrpcTask;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WLog;
import wannabit.io.cosmostaion.utils.WUtil;

import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_KAVA_JOIN_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_KAVA_SWAP_POOLS_INFO;
import static wannabit.io.cosmostaion.base.BaseConstant.TOKEN_KAVA;

public class DepositPoolStep0Fragment extends BaseFragment implements View.OnClickListener, TaskListener {

    private RelativeLayout  mProgress;
    private Button          mCancelBtn, mNextBtn;

    private ImageView       mJoinPoolInput0Img;
    private TextView        mJoinPoolInput0Symbol;
    private EditText        mJoinPoolInput0;
    private ImageView       mJoinPoolInput0ClearBtn;
    private TextView        mJoinPoolInput0Amount, mJoinPoolInput0Denom;
    private Button          mJoinPoolInput01_4Btn, mJoinPoolInput0HalfBtn, mJoinPoolInput03_4Btn, mJoinPoolInput0MaxBtn;

    private ImageView       mJoinPoolInput1Img;
    private TextView        mJoinPoolInput1Symbol;
    private EditText        mJoinPoolInput1;
    private ImageView       mJoinPoolInput1ClearBtn;
    private TextView        mJoinPoolInput1Amount, mJoinPoolInput1Denom;
    private Button          mJoinPoolInput11_4, mJoinPoolInput1Half, mJoinPoolInput13_4, mJoinPoolInput1Max;

    private BigDecimal      mAvailable0MaxAmount, mAvailable1MaxAmount;
    private int             mCoin0Decimal = 6, mCoin1Decimal = 6;
    private String          mCoin0Denom = "", mCoin1Denom = "";
    private BigDecimal      mCoin0Amount = BigDecimal.ZERO, mCoin1Amount = BigDecimal.ZERO;
    private BigDecimal      mDepositRate = BigDecimal.ONE;

    private ArrayList<QueryOuterClass.PoolResponse>         mSwapPool = new ArrayList<>();

    private String          mInDecimalChecker, mInDecimalSetter;
    private String          mOutDecimalChecker,mOutDecimalSetter;

    private boolean         mIsInput0Focused;

    public static DepositPoolStep0Fragment newInstance(Bundle bundle) {
        DepositPoolStep0Fragment fragment = new DepositPoolStep0Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_pool_step0, container, false);
        mCancelBtn                  = rootView.findViewById(R.id.btn_cancel);
        mNextBtn                    = rootView.findViewById(R.id.btn_next);
        mProgress                   = rootView.findViewById(R.id.progress);

        mJoinPoolInput0Img          = rootView.findViewById(R.id.join_pool_input_icon);
        mJoinPoolInput0Symbol       = rootView.findViewById(R.id.join_pool_input_symbol);
        mJoinPoolInput0             = rootView.findViewById(R.id.join_pool_input);
        mJoinPoolInput0ClearBtn     = rootView.findViewById(R.id.join_pool_input_clear);
        mJoinPoolInput0Amount       = rootView.findViewById(R.id.join_pool_input_amount);
        mJoinPoolInput0Denom        = rootView.findViewById(R.id.join_pool_input_amount_denom);
        mJoinPoolInput01_4Btn       = rootView.findViewById(R.id.join_pool_input_1_4);
        mJoinPoolInput0HalfBtn      = rootView.findViewById(R.id.join_pool_input_half);
        mJoinPoolInput03_4Btn       = rootView.findViewById(R.id.join_pool_input_3_4);
        mJoinPoolInput0MaxBtn       = rootView.findViewById(R.id.join_pool_input_max);

        mJoinPoolInput1Img          = rootView.findViewById(R.id.join_pool_output_icon);
        mJoinPoolInput1Symbol       = rootView.findViewById(R.id.join_pool_output_symbol);
        mJoinPoolInput1             = rootView.findViewById(R.id.join_pool_output);
        mJoinPoolInput1ClearBtn     = rootView.findViewById(R.id.join_pool_output_clear);
        mJoinPoolInput1Amount       = rootView.findViewById(R.id.join_pool_output_amount);
        mJoinPoolInput1Denom        = rootView.findViewById(R.id.join_pool_output_amount_denom);
        mJoinPoolInput11_4          = rootView.findViewById(R.id.join_pool_output_1_4);
        mJoinPoolInput1Half         = rootView.findViewById(R.id.join_pool_output_half);
        mJoinPoolInput13_4          = rootView.findViewById(R.id.join_pool_output_3_4);
        mJoinPoolInput1Max          = rootView.findViewById(R.id.join_pool_output_max);

        mJoinPoolInput0ClearBtn.setOnClickListener(this);
        mJoinPoolInput01_4Btn.setOnClickListener(this);
        mJoinPoolInput0HalfBtn.setOnClickListener(this);
        mJoinPoolInput03_4Btn.setOnClickListener(this);
        mJoinPoolInput0MaxBtn.setOnClickListener(this);

        mJoinPoolInput1ClearBtn.setOnClickListener(this);
        mJoinPoolInput11_4.setOnClickListener(this);
        mJoinPoolInput1Half.setOnClickListener(this);
        mJoinPoolInput13_4.setOnClickListener(this);
        mJoinPoolInput1Max.setOnClickListener(this);

        mCancelBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        onFetchPoolInfo();
        return rootView;
    }

    private void onInitView() {
        mProgress.setVisibility(View.GONE);

        BigDecimal txFeeAmount = WUtil.getEstimateGasFeeAmount(getSActivity(), getSActivity().mBaseChain, CONST_PW_TX_KAVA_JOIN_POOL, 0);
        mCoin0Denom = mSwapPool.get(0).getCoins(0).getDenom();
        mCoin1Denom = mSwapPool.get(0).getCoins(1).getDenom();
        mCoin0Decimal = WUtil.getKavaCoinDecimal(getBaseDao(), mCoin0Denom);
        mCoin1Decimal = WUtil.getKavaCoinDecimal(getBaseDao(), mCoin1Denom);

        if (mSwapPool.get(0).getCoins(0).getDenom().equalsIgnoreCase(mCoin0Denom)) {
            mCoin0Amount = new BigDecimal(mSwapPool.get(0).getCoins(0).getAmount());
            mCoin1Amount = new BigDecimal(mSwapPool.get(0).getCoins(1).getAmount());
        } else {
            mCoin0Amount = new BigDecimal(mSwapPool.get(0).getCoins(1).getAmount());
            mCoin1Amount = new BigDecimal(mSwapPool.get(0).getCoins(0).getAmount());
        }

        mAvailable0MaxAmount = getBaseDao().getAvailable(mCoin0Denom);
        if (mCoin0Denom.equalsIgnoreCase(TOKEN_KAVA)) { mAvailable0MaxAmount = mAvailable0MaxAmount.subtract(txFeeAmount); }
        mAvailable1MaxAmount = getBaseDao().getAvailable(mCoin1Denom);
        if (mCoin1Denom.equalsIgnoreCase(TOKEN_KAVA)) { mAvailable1MaxAmount = mAvailable1MaxAmount.subtract(txFeeAmount); }

        setDpDecimals(mCoin0Decimal, mCoin1Decimal);

        WUtil.dpKavaTokenName(getSActivity(), getBaseDao(), mJoinPoolInput0Symbol, mCoin0Denom);
        WUtil.dpKavaTokenName(getSActivity(), getBaseDao(), mJoinPoolInput1Symbol, mCoin1Denom);
        WUtil.DpKavaTokenImg(getBaseDao(), mJoinPoolInput0Img, mCoin0Denom);
        WUtil.DpKavaTokenImg(getBaseDao(), mJoinPoolInput1Img, mCoin1Denom);
        WDp.showCoinDp(getSActivity(), getBaseDao(), WUtil.dpKavaTokenName(getSActivity(), getBaseDao(), mJoinPoolInput0Denom, mCoin0Denom), mAvailable0MaxAmount.toString(), mJoinPoolInput0Denom, mJoinPoolInput0Amount, BaseChain.KAVA_MAIN);
        WDp.showCoinDp(getSActivity(), getBaseDao(), WUtil.dpKavaTokenName(getSActivity(), getBaseDao(), mJoinPoolInput1Denom, mCoin1Denom), mAvailable1MaxAmount.toString(), mJoinPoolInput1Denom, mJoinPoolInput1Amount, BaseChain.KAVA_MAIN);

        mDepositRate = mCoin1Amount.divide(mCoin0Amount, 18, RoundingMode.DOWN);

        onAddAmountWatcher();

        mJoinPoolInput0.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                mIsInput0Focused = focused;
            }
        });

        mJoinPoolInput1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                mIsInput0Focused = !focused;
            }
        });
    }

    private void onAddAmountWatcher() {
        mJoinPoolInput0.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable et) {
                String es = et.toString().trim();
                onUpdateInput1Et(es);
                if (TextUtils.isEmpty(es)) {
                    mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                } else if (es.startsWith(".")) {
                    mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                    mJoinPoolInput0.setText("");
                } else if (es.endsWith(".")) {
                    mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                    mJoinPoolInput0.setVisibility(View.VISIBLE);
                } else if(mJoinPoolInput0.length() > 1 && es.startsWith("0") && !es.startsWith("0.")) {
                    mJoinPoolInput0.setText("0");
                    mJoinPoolInput0.setSelection(1);
                }

                if (es.equals(mInDecimalChecker)) {
                    mJoinPoolInput0.setText(mInDecimalSetter);
                    mJoinPoolInput0.setSelection(mCoin0Decimal + 1);
                } else {
                    try {
                        final BigDecimal inputAmount = new BigDecimal(es);
                        if (BigDecimal.ZERO.compareTo(inputAmount) >= 0 ){
                            mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                            return;
                        }

                        BigDecimal checkPosition = inputAmount.movePointRight(mCoin0Decimal);
                        BigDecimal checkMax = checkPosition.setScale(0, RoundingMode.DOWN);
                        if (checkPosition.compareTo(checkMax) != 0 || !checkPosition.equals(checkMax)) {
                            String recover = es.substring(0, es.length() - 1);
                            mJoinPoolInput0.setText(recover);
                            mJoinPoolInput0.setSelection(recover.length());
                            return;
                        }

                        if (inputAmount.compareTo(mAvailable0MaxAmount.movePointLeft(mCoin0Decimal).setScale(mCoin0Decimal, RoundingMode.CEILING)) > 0) {
                            mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                        } else {
                            mJoinPoolInput0.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                        }
                        mJoinPoolInput0.setSelection(mJoinPoolInput0.getText().length());

                    } catch (Exception e) { }
                }
            }
        });

        mJoinPoolInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable et) {
                String es = et.toString().trim();
                onUpdateInput0Et(es);
                if (TextUtils.isEmpty(es)) {
                    mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                } else if (es.startsWith(".")) {
                    mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                    mJoinPoolInput1.setText("");
                } else if (es.endsWith(".")) {
                    mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                    mJoinPoolInput1.setVisibility(View.VISIBLE);
                } else if(mJoinPoolInput1.length() > 1 && es.startsWith("0") && !es.startsWith("0.")) {
                    mJoinPoolInput1.setText("0");
                    mJoinPoolInput1.setSelection(1);
                }

                if (es.equals(mOutDecimalChecker)) {
                    mJoinPoolInput1.setText(mOutDecimalSetter);
                    mJoinPoolInput1.setSelection(mCoin1Decimal + 1);
                } else {
                    try {
                        final BigDecimal inputAmount = new BigDecimal(es);
                        if (BigDecimal.ZERO.compareTo(inputAmount) >= 0 ){
                            mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                            return;
                        }

                        BigDecimal checkPosition = inputAmount.movePointRight(mCoin1Decimal);
                        BigDecimal checkMax = checkPosition.setScale(0, RoundingMode.DOWN);
                        if (checkPosition.compareTo(checkMax) != 0 || !checkPosition.equals(checkMax)) {
                            String recover = es.substring(0, es.length() - 1);
                            mJoinPoolInput1.setText(recover);
                            mJoinPoolInput1.setSelection(recover.length());
                            return;
                        }

                        if (inputAmount.compareTo(mAvailable1MaxAmount.movePointLeft(mCoin1Decimal).setScale(mCoin1Decimal, RoundingMode.CEILING)) > 0) {
                            mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box_error));
                        } else {
                            mJoinPoolInput1.setBackground(getResources().getDrawable(R.drawable.edittext_box));
                        }
                        mJoinPoolInput1.setSelection(mJoinPoolInput1.getText().length());

                    } catch (Exception e) { }
                }
            }
        });
    }

    private void onUpdateInput0Et(String outputs) {
        WLog.w("onUpdateInput0Et " + outputs + "  " + mIsInput0Focused);
        if (!mIsInput0Focused) {
            try {
                final BigDecimal outputAmount = new BigDecimal(outputs).movePointRight(mCoin1Decimal);
                if (outputAmount.equals(BigDecimal.ZERO)) { mJoinPoolInput0.setText(""); return; }
                BigDecimal inputAmount = outputAmount.divide(mDepositRate, 0, RoundingMode.DOWN);
                mJoinPoolInput0.setText(inputAmount.movePointLeft(mCoin0Decimal).toPlainString());

            } catch (Exception e) {
                mJoinPoolInput0.setText("");
            }
        }
    }

    private void onUpdateInput1Et(String inputs) {
        WLog.w("onUpdateInput1Et " + inputs + "  " + mIsInput0Focused);
        if (mIsInput0Focused) {
            try {
                final BigDecimal inputAmount = new BigDecimal(inputs).movePointRight(mCoin0Decimal);
                if (inputAmount.equals(BigDecimal.ZERO)) { mJoinPoolInput1.setText(""); return; }
                BigDecimal OutputAmount = inputAmount.multiply(mDepositRate).setScale(0, RoundingMode.DOWN);
                mJoinPoolInput1.setText(OutputAmount.movePointLeft(mCoin1Decimal).toPlainString());

            } catch (Exception e) {
                mJoinPoolInput1.setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mJoinPoolInput01_4Btn)) {
            mJoinPoolInput0.requestFocus();
            BigDecimal cal = mAvailable0MaxAmount.multiply(new BigDecimal(0.25)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput0.setText(cal.movePointLeft(mCoin0Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput0HalfBtn)) {
            mJoinPoolInput0.requestFocus();
            BigDecimal cal = mAvailable0MaxAmount.multiply(new BigDecimal(0.5)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput0.setText(cal.movePointLeft(mCoin0Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput03_4Btn)) {
            mJoinPoolInput0.requestFocus();
            BigDecimal cal = mAvailable0MaxAmount.multiply(new BigDecimal(0.75)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput0.setText(cal.movePointLeft(mCoin0Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput0MaxBtn)) {
            mJoinPoolInput0.requestFocus();
            mJoinPoolInput0.setText(mAvailable0MaxAmount.movePointLeft(mCoin0Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput0ClearBtn)) {
            mJoinPoolInput0.requestFocus();
            mJoinPoolInput0.setText("");
        }

        else if (v.equals(mJoinPoolInput11_4)) {
            mJoinPoolInput1.requestFocus();
            BigDecimal cal = mAvailable1MaxAmount.multiply(new BigDecimal(0.25)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput1.setText(cal.movePointLeft(mCoin1Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput1Half)) {
            mJoinPoolInput1.requestFocus();
            BigDecimal cal = mAvailable1MaxAmount.multiply(new BigDecimal(0.5)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput1.setText(cal.movePointLeft(mCoin1Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput13_4)) {
            mJoinPoolInput1.requestFocus();
            BigDecimal cal = mAvailable1MaxAmount.multiply(new BigDecimal(0.75)).setScale(0, RoundingMode.DOWN);
            mJoinPoolInput1.setText(cal.movePointLeft(mCoin1Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput1Max)) {
            mJoinPoolInput1.requestFocus();
            mJoinPoolInput1.setText(mAvailable1MaxAmount.movePointLeft(mCoin1Decimal).toPlainString());

        } else if (v.equals(mJoinPoolInput1ClearBtn)) {
            mJoinPoolInput1.requestFocus();
            mJoinPoolInput1.setText("");
        }

        else if (v.equals(mCancelBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mNextBtn)) {
            if (isValidateJoinPool()) {
                getSActivity().onNextStep();
            } else {
                Toast.makeText(getContext(), R.string.error_invalid_amounts, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidateJoinPool() {
        try {
            BigDecimal InputAmountTemp = new BigDecimal(mJoinPoolInput0.getText().toString().trim());
            if (InputAmountTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
            if (InputAmountTemp.compareTo(mAvailable0MaxAmount.movePointLeft(mCoin0Decimal).setScale(mCoin0Decimal, RoundingMode.CEILING)) > 0) return false;

            BigDecimal OutputAmountTemp = new BigDecimal(mJoinPoolInput1.getText().toString().trim());
            if (OutputAmountTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
            if (OutputAmountTemp.compareTo(mAvailable1MaxAmount.movePointLeft(mCoin1Decimal).setScale(mCoin1Decimal, RoundingMode.CEILING)) > 0) return false;

            getSActivity().mKavaPoolTokenA = new Coin(mCoin0Denom, InputAmountTemp.movePointRight(mCoin0Decimal).toPlainString());
            getSActivity().mKavaPoolTokenB = new Coin(mCoin1Denom, OutputAmountTemp.movePointRight(mCoin1Decimal).toPlainString());

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void setDpDecimals(int indecimals, int outdecimals) {
        mInDecimalChecker = "0.";
        mInDecimalSetter = "0.";
        mOutDecimalChecker = "0.";
        mOutDecimalSetter = "0.";
        for (int i = 0; i < indecimals; i ++) {
            mInDecimalChecker = mInDecimalChecker + "0";
        }
        for (int i = 0; i < indecimals-1; i ++) {
            mInDecimalSetter = mInDecimalSetter + "0";
        }
        for (int i = 0; i < outdecimals; i ++) {
            mOutDecimalChecker = mOutDecimalChecker + "0";
        }
        for (int i = 0; i < outdecimals-1; i ++) {
            mOutDecimalSetter = mOutDecimalSetter + "0";
        }
    }

    private int mTaskCount;
    public void onFetchPoolInfo() {
        mTaskCount = 1;
        new KavaSwapPoolInfoGrpcTask(getBaseApplication(), this, getSActivity().mBaseChain, getSActivity().mKavaSwapPool.getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTaskResponse(TaskResult result) {
        mTaskCount--;
        if (result.taskType == TASK_GRPC_FETCH_KAVA_SWAP_POOLS_INFO) {
            if (result.isSuccess && result.resultData != null) {
                mSwapPool = (ArrayList<QueryOuterClass.PoolResponse>) result.resultData;
            }
        }
        if (mTaskCount == 0) {
            onInitView();
        }
    }

    private DepositPoolActivity getSActivity() { return (DepositPoolActivity)getBaseActivity(); }

}

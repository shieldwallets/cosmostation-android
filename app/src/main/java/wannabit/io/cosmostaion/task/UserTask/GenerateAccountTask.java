package wannabit.io.cosmostaion.task.UserTask;

import org.bitcoinj.crypto.DeterministicKey;

import java.util.ArrayList;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.crypto.CryptoHelper;
import wannabit.io.cosmostaion.crypto.EncResult;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WKey;

import static wannabit.io.cosmostaion.base.BaseChain.INJ_MAIN;
import static wannabit.io.cosmostaion.base.BaseChain.OKEX_MAIN;

public class GenerateAccountTask extends CommonTask {
    private BaseChain       mBaseChain;
    private int             mCustomPath;

    private ArrayList<BaseChain> mHideChains = new ArrayList<>();

    public GenerateAccountTask(BaseApplication app, BaseChain baseChain, TaskListener listener, int customPath) {
        super(app, listener);
        this.mBaseChain = baseChain;
        this.mCustomPath = customPath;
        this.mResult.taskType = BaseConstant.TASK_INIT_ACCOUNT;
    }


    /**
     *
     * @param strings
     *  strings[0] : path
     *  strings[1] : entorpy seed
     *  strings[2] : word size
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            long id = mApp.getBaseDao().onInsertAccount(onGenAccount(strings[1], strings[0], strings[2]));
            if(id > 0) {
                mResult.isSuccess = true;
                mHideChains = mApp.getBaseDao().userHideChains();
                if (mHideChains.contains(mBaseChain)) {
                    int position = mHideChains.indexOf(mBaseChain);
                    if (position >= 0) {
                        mHideChains.remove(position);
                    }
                    mApp.getBaseDao().setUserHidenChains(mHideChains);
                }
                mApp.getBaseDao().setLastUser(id);
                mApp.getBaseDao().setLastChain(mBaseChain.getChain());

            } else {
                mResult.errorMsg = "Already existed account";
                mResult.errorCode = 7001;
            }

        } catch (Exception e){

        }
        return mResult;
    }



    private Account onGenAccount(String entropy, String path, String msize) {
        Account newAccount          = Account.getNewInstance();
        EncResult encR              = CryptoHelper.doEncryptData(mApp.getString(R.string.key_mnemonic)+ newAccount.uuid, entropy, false);

        newAccount.address          = WKey.getCreateDpAddressFromEntropy(mBaseChain, entropy, Integer.parseInt(path), mCustomPath);
        newAccount.baseChain        = mBaseChain.getChain();
        newAccount.hasPrivateKey    = true;
        newAccount.resource         = encR.getEncDataString();
        newAccount.spec             = encR.getIvDataString();
        newAccount.fromMnemonic     = true;
        newAccount.path             = path;
        newAccount.msize            = Integer.parseInt(msize);
        newAccount.importTime       = System.currentTimeMillis();
        newAccount.customPath       = mCustomPath;
        return newAccount;

    }

}

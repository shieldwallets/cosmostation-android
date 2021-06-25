package wannabit.io.cosmostaion.network.res;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.utils.WLog;

public class ResApiNewTxListCustom {
    @SerializedName("header")
    public Header header;

    @SerializedName("data")
    public Data data;

    public class Header {
        @SerializedName("id")
        public int id;

        @SerializedName("chain_id")
        public String chain_id;

        @SerializedName("block_id")
        public int block_id;
    }

    public class Data {
        @SerializedName("height")
        public String height;

        @SerializedName("txhash")
        public String txhash;

        @SerializedName("codespace")
        public String codespace;

        @SerializedName("code")
        public int code;

        @SerializedName("raw_log")
        public String raw_log;

        @SerializedName("info")
        public String info;

        @SerializedName("gas_wanted")
        public int gas_wanted;

        @SerializedName("gas_used")
        public int gas_used;

        @SerializedName("tx")
        public Tx tx;

        @SerializedName("timestamp")
        public String timestamp;
    }

    public class Tx {
        @SerializedName("@type")
        public String type;

        @SerializedName("body")
        public Body body;

    }

    public class Body {
        @SerializedName("messages")
        public ArrayList<Object> messages;

        @SerializedName("memo")
        public String memo;
    }


    public boolean isSuccess() {
        if (data.code > 0) {
            return false;
        }
        return true;
    }

    public JSONArray getMsgs() {
        if (data != null && data.tx != null && data.tx.body != null && data.tx.body.messages != null) {
            return new JSONArray(data.tx.body.messages);
        }
        return null;
    }

    public int getMsgCnt() {
        if (getMsgs() != null) {
            return getMsgs().length();
        }
        return 0;
    }

    public String getMsgType(Context c, String address) {
        if (getMsgCnt() == 0) {
            return c.getString(R.string.tx_known);
        } else {
            try {
                String result = c.getString(R.string.tx_known);;
                if (getMsgCnt() == 2) {
                    String msgType0 = "";
                    String msgType1 = "";
                    try {
                        msgType0 = getMsgs().getJSONObject(0).getString("@type");
                    } catch (Exception e) {
                    }
                    try {
                        msgType0 = getMsgs().getJSONObject(0).getString("type");
                    } catch (Exception e) {
                    }
                    try {
                        msgType1 = getMsgs().getJSONObject(1).getString("@type");
                    } catch (Exception e) {
                    }
                    try {
                        msgType1 = getMsgs().getJSONObject(1).getString("type");
                    } catch (Exception e) {
                    }
                    if (msgType0.contains("MsgWithdrawDelegatorReward") && msgType1.contains("MsgDelegate")) {
                        return c.getString(R.string.tx_reinvest);
                    }
                }
                String msgType = "";
                try {
                    msgType = getMsgs().getJSONObject(0).getString("@type");
                } catch (Exception e) {
                }
                try {
                    msgType = getMsgs().getJSONObject(0).getString("type");
                } catch (Exception e) {
                }
                if (msgType.contains("MsgDelegate")) {
                    result = c.getString(R.string.tx_delegate);
                } else if (msgType.contains("MsgUndelegate")) {
                    result = c.getString(R.string.tx_undelegate);
                } else if (msgType.contains("MsgWithdrawDelegatorReward") || msgType.contains("MsgWithdrawDelegationReward")) {
                    result = c.getString(R.string.tx_get_reward);
                } else if (msgType.contains("MsgSend")) {
                    try {
                        if (getMsgs().getJSONObject(0).getString("to_address").equals(address)) {
                            result = c.getString(R.string.tx_receive);
                        } else if (getMsgs().getJSONObject(0).getString("from_address").equals(address)) {
                            result = c.getString(R.string.tx_send);
                        } else {
                            result = c.getString(R.string.tx_transfer);
                        }
                    } catch (Exception e) {
                        result = c.getString(R.string.tx_transfer);
                    }
                } else if (msgType.contains("MsgMultiSend")) {
                    result = c.getString(R.string.tx_transfer);
                } else if (msgType.contains("MsgBeginRedelegate")) {
                    result = c.getString(R.string.tx_redelegate);
                } else if (msgType.contains("MsgSetWithdrawAddress") || msgType.contains("MsgModifyWithdrawAddress")) {
                    result = c.getString(R.string.tx_change_reward_address);
                } else if (msgType.contains("MsgCreateValidator")) {
                    result = c.getString(R.string.tx_create_validator);
                } else if (msgType.contains("MsgEditValidator")) {
                    result = c.getString(R.string.tx_edit_validator);
                } else if (msgType.contains("MsgUnjail")) {
                    result = c.getString(R.string.tx_unjail);
                } else if (msgType.contains("MsgSubmitProposal")) {
                    result = c.getString(R.string.tx_submit_proposal);
                } else if (msgType.contains("MsgVote")) {
                    result = c.getString(R.string.tx_vote);
                } else if (msgType.contains("MsgDeposit")) {
                    result = c.getString(R.string.tx_deposit);
                } else if (msgType.contains("MsgWithdrawValidatorCommission")) {
                    result = c.getString(R.string.tx_get_commission);
                } else if (msgType.contains("MsgCreateBid")) {
                    result = c.getString(R.string.tx_create_bid);
                } else if (msgType.contains("MsgCloseBid")) {
                    result = c.getString(R.string.tx_close_bid);
                } else if (msgType.contains("MsgCreateLease")) {
                    result = c.getString(R.string.tx_create_lease);
                } else if (msgType.contains("MsgWithdrawLease")) {
                    result = c.getString(R.string.tx_withdraw_lease);
                } else if (msgType.contains("MsgCreateDeployment")) {
                    result = c.getString(R.string.tx_create_deployment);
                } else if (msgType.contains("MsgUpdateDeployment")) {
                    result = c.getString(R.string.tx_update_deployment);
                } else if (msgType.contains("MsgCloseDeployment")) {
                    result = c.getString(R.string.tx_close_deployment);
                } else if (msgType.contains("MsgCreateCertificate")) {
                    result = c.getString(R.string.tx_create_certificate);
                } else if (msgType.contains("MsgRevokeCertificate")) {
                    result = c.getString(R.string.tx_revoke_certificate);
                } else if (msgType.contains("MsgUpdateClient")) {
                    result = c.getString(R.string.tx_ibc_update_client);
                } else if (msgType.contains("MsgTransfer") || msgType.contains("ibc")) {
                    result = c.getString(R.string.tx_ibc_transfer);
                } else if (msgType.contains("MsgMintNFT")) {
                    result = "NFT Mint";
                } else if (msgType.contains("MsgTransferNFT")) {
                    result = "NFT Transfer";
                } else if (msgType.contains("MsgEditNFT")) {
                    result = "NFT Edit";
                } else if (msgType.contains("MsgIssueDenom")) {
                    result = "NFT Issue";
                } else if (msgType.contains("MsgRequestRandom")) {
                    result = "Random Request";
                }
                if (getMsgCnt() > 1) {
                    result = result + "\n+ " + (getMsgCnt() - 1);
                }
                return result;
            } catch (Exception e) {
                WLog.w("Exception " + e.getMessage());
            }
            return c.getString(R.string.tx_known);
        }
    }

    public Coin getDpCoin() {
        ArrayList<Coin> coins = new ArrayList<>();
        String denom = "";
        String amount = "";
        if (getMsgCnt() == 0 || getMsgCnt() > 1) { return new Coin(denom , amount); }

            String msgType = "";
            try {
                msgType = getMsgs().getJSONObject(0).getString("@type");
            } catch (Exception e) {
            }

            try {
                msgType = getMsgs().getJSONObject(0).getString("type");
            } catch (Exception e) {
            }
            WLog.w("Type : " + msgType);

            if (msgType.contains("MsgSend")) {
                try {
                    denom = getMsgs().getJSONObject(0).getJSONArray("amount").getJSONObject(0).getString("denom");
                    amount = getMsgs().getJSONObject(0).getJSONArray("amount").getJSONObject(0).getString("amount");
                    coins.add(new Coin(denom, amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (msgType.contains("MsgDelegate") || msgType.contains("MsgUndelegate") || msgType.contains("MsgBeginRedelegate")) {
                try {
                    denom = getMsgs().getJSONObject(0).getJSONObject("amount").getString("denom");
                    amount = getMsgs().getJSONObject(0).getJSONObject("amount").getString("amount");
                    coins.add(new Coin(denom, amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msgType.contains("ibc") && msgType.contains("MsgTransfer")) {
                try {
                    denom = getMsgs().getJSONObject(0).getJSONObject("token").getString("denom");
                    amount = getMsgs().getJSONObject(0).getJSONObject("token").getString("amount");
                    coins.add(new Coin(denom, amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new Coin(denom, amount);
        }


}

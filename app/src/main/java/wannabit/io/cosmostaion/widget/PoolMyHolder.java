package wannabit.io.cosmostaion.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cosmos.base.v1beta1.CoinOuterClass;
import kava.swap.v1beta1.QueryOuterClass;
import osmosis.gamm.poolmodels.balancer.BalancerPool;
import tendermint.liquidity.v1beta1.Liquidity;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.cosmos.GravityListActivity;
import wannabit.io.cosmostaion.activities.chains.kava.DAppsList5Activity;
import wannabit.io.cosmostaion.activities.chains.osmosis.LabsListActivity;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WLog;
import wannabit.io.cosmostaion.utils.WUtil;

public class PoolMyHolder extends BaseHolder {
    CardView itemRoot;
    TextView itemMyPoolType;
    TextView itemMyTotalDepositValue;
    TextView itemMyTotalDepositAmount0, itemMyTotalDepositSymbol0, itemMyTotalDepositAmount1, itemMyTotalDepositSymbol1;
    TextView itemMypoolDepositValue;
    TextView itemMyDepositAmount0, itemMyDepositSymbol0, itemMyDepositAmount1, itemMyDepositSymbol1;
    TextView itemMyAvailableAmount0, itemMyAvailableSymbol0, itemMyAvailableAmount1, itemMyAvailableSymbol1;

    public PoolMyHolder(@NonNull View itemView) {
        super(itemView);
        itemRoot = itemView.findViewById(R.id.card_root);
        itemMyPoolType = itemView.findViewById(R.id.mypool_market_type);
        itemMyTotalDepositValue = itemView.findViewById(R.id.mypool_total_liquidity_value);
        itemMyTotalDepositAmount0 = itemView.findViewById(R.id.mypool_total_liquidity_amount1);
        itemMyTotalDepositSymbol0 = itemView.findViewById(R.id.mypool_total_liquidity_symbol1);
        itemMyTotalDepositAmount1 = itemView.findViewById(R.id.mypool_total_liquidity_amount2);
        itemMyTotalDepositSymbol1 = itemView.findViewById(R.id.mypool_total_liquidity_symbol2);

        itemMypoolDepositValue  = itemView.findViewById(R.id.mypool_deposit_value);
        itemMyDepositAmount0    = itemView.findViewById(R.id.mypool_deposit_amout0);
        itemMyDepositSymbol0    = itemView.findViewById(R.id.mypool_deposit_symbol0);
        itemMyDepositAmount1    = itemView.findViewById(R.id.mypool_deposit_amout1);
        itemMyDepositSymbol1    = itemView.findViewById(R.id.mypool_deposit_symbol1);

        itemMyAvailableAmount0  = itemView.findViewById(R.id.my_available_amount0);
        itemMyAvailableSymbol0  = itemView.findViewById(R.id.my_available_symbol0);
        itemMyAvailableAmount1  = itemView.findViewById(R.id.my_available_amount1);
        itemMyAvailableSymbol1  = itemView.findViewById(R.id.my_available_symbol1);
    }

    @Override
    public void onBindOsmoMyPool(Context context, BaseActivity activity, BaseData baseData, BalancerPool.Pool myPool) {
        itemRoot.setCardBackgroundColor(context.getResources().getColor(R.color.colorTransBgOsmosis));
        itemMyPoolType.setTextColor(WDp.getChainColor(context, BaseChain.OSMOSIS_MAIN));
        Coin coin0 = new Coin(myPool.getPoolAssets(0).getToken().getDenom(), myPool.getPoolAssets(0).getToken().getAmount());
        Coin coin1 = new Coin(myPool.getPoolAssets(1).getToken().getDenom(), myPool.getPoolAssets(1).getToken().getAmount());

        itemMyPoolType.setText("#" + myPool.getId() + " " + WUtil.dpOsmosisTokenName(baseData, coin0.denom) + "/" + WUtil.dpOsmosisTokenName(baseData, coin1.denom));

        // Total deposit
        BigDecimal coin0Value = WDp.usdValue(baseData, baseData.getBaseDenom(coin0.denom), new BigDecimal(coin0.amount), WUtil.getOsmosisCoinDecimal(baseData, coin0.denom));
        BigDecimal coin1Value = WDp.usdValue(baseData, baseData.getBaseDenom(coin1.denom), new BigDecimal(coin1.amount), WUtil.getOsmosisCoinDecimal(baseData, coin1.denom));
        BigDecimal PoolValue = coin0Value.add(coin1Value);
        itemMyTotalDepositValue.setText(WDp.getDpRawDollor(context, PoolValue, 2));

        WDp.showCoinDp(context, baseData, coin0, itemMyTotalDepositSymbol0, itemMyTotalDepositAmount0, BaseChain.OSMOSIS_MAIN);
        WDp.showCoinDp(context, baseData, coin1, itemMyTotalDepositSymbol1, itemMyTotalDepositAmount1, BaseChain.OSMOSIS_MAIN);

        //deposit
        BigDecimal lpCoin = baseData.getAvailable("gamm/pool/" + myPool.getId());
        BigDecimal lpCoinPrice = WUtil.getOsmoLpTokenPerUsdPrice(baseData, myPool);
        BigDecimal lpCoinValue = new BigDecimal(lpCoin.toPlainString()).multiply(lpCoinPrice).movePointLeft(18).setScale(2,RoundingMode.DOWN);
        itemMypoolDepositValue.setText(WDp.getDpRawDollor(context, lpCoinValue, 2));

        BigDecimal coin0MyShareAmount = WUtil.getMyShareLpAmount(baseData, myPool, coin0.denom);
        BigDecimal coin1MyShareAmount = WUtil.getMyShareLpAmount(baseData, myPool, coin1.denom);

        WDp.showCoinDp(context, baseData, coin0.denom, coin0MyShareAmount.toPlainString(), itemMyDepositSymbol0, itemMyDepositAmount0, BaseChain.OSMOSIS_MAIN);
        WDp.showCoinDp(context, baseData, coin1.denom, coin1MyShareAmount.toPlainString(), itemMyDepositSymbol1, itemMyDepositAmount1, BaseChain.OSMOSIS_MAIN);

        // available
        BigDecimal availableCoin0 = baseData.getAvailable(coin0.denom);
        Coin Coin0 = new Coin(myPool.getPoolAssets(0).getToken().getDenom(), availableCoin0.toPlainString());
        BigDecimal availableCoin1 = baseData.getAvailable(coin1.denom);
        Coin Coin1 = new Coin(myPool.getPoolAssets(1).getToken().getDenom(), availableCoin1.toPlainString());

        WDp.showCoinDp(context, baseData, Coin0, itemMyAvailableSymbol0, itemMyAvailableAmount0, BaseChain.OSMOSIS_MAIN);
        WDp.showCoinDp(context, baseData, Coin1, itemMyAvailableSymbol1, itemMyAvailableAmount1, BaseChain.OSMOSIS_MAIN);

        itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WLog.w("PoolMyHolder onClick ");
                ((LabsListActivity)activity).onClickMyPool(myPool.getId());
            }
        });
    }

    @Override
    public void onBindKavaMyPool(Context context, BaseActivity activity, BaseData baseData, QueryOuterClass.PoolResponse myPool, QueryOuterClass.DepositResponse myDeposit) {
        itemRoot.setCardBackgroundColor(context.getResources().getColor(R.color.colorTransBgKava));
        itemMyPoolType.setTextColor(WDp.getChainColor(context, BaseChain.KAVA_MAIN));

        CoinOuterClass.Coin coin0 = myPool.getCoins(0);
        CoinOuterClass.Coin coin1 = myPool.getCoins(1);
        int coin0Decimal = WUtil.getKavaCoinDecimal(baseData, coin0.getDenom());
        int coin1Decimal = WUtil.getKavaCoinDecimal(baseData, coin1.getDenom());
        BigDecimal coin0price = WDp.getKavaPriceFeed(baseData, coin0.getDenom());
        BigDecimal coin1price = WDp.getKavaPriceFeed(baseData, coin1.getDenom());
        BigDecimal coin0Value = new BigDecimal(coin0.getAmount()).multiply(coin0price).movePointLeft(coin0Decimal).setScale(2, RoundingMode.DOWN);
        BigDecimal coin1Value = new BigDecimal(coin1.getAmount()).multiply(coin1price).movePointLeft(coin1Decimal).setScale(2, RoundingMode.DOWN);

        itemMyPoolType.setText(WUtil.getKavaTokenName(baseData, coin0.getDenom()) + " : " + WUtil.getKavaTokenName(baseData, coin1.getDenom()));
        BigDecimal poolValue = coin0Value.add(coin1Value);
        itemMyTotalDepositValue.setText(WDp.getDpRawDollor(context, poolValue, 2));

        // Total deposit
        WUtil.dpKavaTokenName(context, baseData, itemMyTotalDepositSymbol0, coin0.getDenom());
        WUtil.dpKavaTokenName(context, baseData, itemMyTotalDepositSymbol1, coin1.getDenom());
        itemMyTotalDepositAmount0.setText(WDp.getDpAmount2(context, new BigDecimal(coin0.getAmount()), coin0Decimal, 6));
        itemMyTotalDepositAmount1.setText(WDp.getDpAmount2(context, new BigDecimal(coin1.getAmount()), coin1Decimal, 6));

        // deposit
        CoinOuterClass.Coin my0 = myDeposit.getSharesValue(0);
        CoinOuterClass.Coin my1 = myDeposit.getSharesValue(1);
        BigDecimal my0Value = new BigDecimal(my0.getAmount()).multiply(coin0price).movePointLeft(coin0Decimal).setScale(2, RoundingMode.DOWN);
        BigDecimal my1Value = new BigDecimal(my1.getAmount()).multiply(coin1price).movePointLeft(coin1Decimal).setScale(2, RoundingMode.DOWN);
        BigDecimal myShareValue = my0Value.add(my1Value);
        itemMypoolDepositValue.setText(WDp.getDpRawDollor(context, myShareValue, 2));

        WUtil.dpKavaTokenName(context, baseData, itemMyDepositSymbol0, my0.getDenom());
        WUtil.dpKavaTokenName(context, baseData, itemMyDepositSymbol1, my1.getDenom());
        itemMyDepositAmount0.setText(WDp.getDpAmount2(context, new BigDecimal(my0.getAmount()), coin0Decimal, 6));
        itemMyDepositAmount1.setText(WDp.getDpAmount2(context, new BigDecimal(my1.getAmount()), coin1Decimal, 6));

        // available
        BigDecimal availableCoin0 = baseData.getAvailable(coin0.getDenom());
        BigDecimal availableCoin1 = baseData.getAvailable(coin1.getDenom());

        WUtil.dpKavaTokenName(context, baseData, itemMyAvailableSymbol0, coin0.getDenom());
        WUtil.dpKavaTokenName(context, baseData, itemMyAvailableSymbol1, coin1.getDenom());
        itemMyAvailableAmount0.setText(WDp.getDpAmount2(context, availableCoin0, coin0Decimal, 6));
        itemMyAvailableAmount1.setText(WDp.getDpAmount2(context, availableCoin1, coin1Decimal, 6));

        itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DAppsList5Activity)activity).onClickMyPool(myPool, myDeposit);
            }
        });
    }

    @Override
    public void onBindGDexMyPool(Context context, GravityListActivity activity, BaseData baseData, Liquidity.Pool mypool) {
        itemRoot.setCardBackgroundColor(context.getResources().getColor(R.color.colorTransBgCosmos));
        itemMyPoolType.setTextColor(WDp.getChainColor(context, BaseChain.COSMOS_MAIN));

        String coin0Denom = mypool.getReserveCoinDenoms(0);
        String coin1Denom = mypool.getReserveCoinDenoms(1);
        BigDecimal coin0Amount = activity.getLpAmount(mypool.getReserveAccountAddress(), coin0Denom);
        BigDecimal coin1Amount = activity.getLpAmount(mypool.getReserveAccountAddress(), coin1Denom);
        int coin0Decimal = WUtil.getCosmosCoinDecimal(baseData, coin0Denom);
        int coin1Decimal = WUtil.getCosmosCoinDecimal(baseData, coin1Denom);

        itemMyPoolType.setText("#" + mypool.getId() + " " + WUtil.dpCosmosTokenName(baseData, coin0Denom) + " : " + WUtil.dpCosmosTokenName(baseData, coin1Denom));

        // Total deposit
        BigDecimal PoolValue = activity.getGdexPoolValue(mypool);
        itemMyTotalDepositValue.setText(WDp.getDpRawDollor(context, PoolValue, 2));

        WUtil.dpCosmosTokenName(context, baseData, itemMyTotalDepositSymbol0, coin0Denom);
        WUtil.dpCosmosTokenName(context, baseData, itemMyTotalDepositSymbol1, coin1Denom);
        itemMyTotalDepositAmount0.setText(WDp.getDpAmount2(context, coin0Amount, coin0Decimal, 6));
        itemMyTotalDepositAmount1.setText(WDp.getDpAmount2(context, coin1Amount, coin1Decimal, 6));

        //deposit
        BigDecimal myPoolValue = activity.getGdexMyShareValue(mypool);
        WUtil.dpCosmosTokenName(context, baseData, itemMyDepositSymbol0, coin0Denom);
        WUtil.dpCosmosTokenName(context, baseData, itemMyDepositSymbol1, coin1Denom);
        itemMypoolDepositValue.setText(WDp.getDpRawDollor(context, myPoolValue, 2));
        itemMyDepositAmount0.setText(WDp.getDpAmount2(context, activity.getGdexMyShareAmount(mypool, coin0Denom), coin0Decimal, 6));
        itemMyDepositAmount1.setText(WDp.getDpAmount2(context, activity.getGdexMyShareAmount(mypool, coin1Denom), coin1Decimal, 6));

        // available
        BigDecimal availableCoin0 = baseData.getAvailable(coin0Denom);
        Coin Coin0 = new Coin(coin0Denom, availableCoin0.toPlainString());
        BigDecimal availableCoin1 = baseData.getAvailable(coin1Denom);
        Coin Coin1 = new Coin(coin1Denom, availableCoin1.toPlainString());

        WDp.showCoinDp(context, baseData, Coin0, itemMyAvailableSymbol0, itemMyAvailableAmount0, BaseChain.COSMOS_MAIN);
        WDp.showCoinDp(context, baseData, Coin1, itemMyAvailableSymbol1, itemMyAvailableAmount1, BaseChain.COSMOS_MAIN);

        itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WLog.w("PoolMyHolder onClick ");
                (activity).onClickMyPool(mypool.getId());
            }
        });
    }
}

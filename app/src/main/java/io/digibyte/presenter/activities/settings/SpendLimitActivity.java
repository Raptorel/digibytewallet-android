package io.digibyte.presenter.activities.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.digibyte.R;
import io.digibyte.presenter.activities.util.BRActivity;
import io.digibyte.tools.manager.FontManager;
import io.digibyte.tools.security.BRKeyStore;
import io.digibyte.tools.util.BRCurrency;


public class SpendLimitActivity extends BRActivity {
    private static final String TAG = SpendLimitActivity.class.getName();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<LimitHolder> adapter;
    List<Integer> items = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_limit);
        recyclerView = findViewById(R.id.limit_list);
        adapter = new LimitAdaptor(this);
        items.add(getAmountByStep(0).intValue());
        items.add(getAmountByStep(1).intValue());
        items.add(getAmountByStep(2).intValue());
        items.add(getAmountByStep(3).intValue());
        items.add(getAmountByStep(4).intValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private class LimitHolder extends RecyclerView.ViewHolder {

        private TextView limit;
        private ImageView check;
        private View divider;

        public LimitHolder(View itemView) {
            super(itemView);
            limit = itemView.findViewById(R.id.currency_item_text);
            check = itemView.findViewById(R.id.currency_checkmark);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    //satoshis
    private BigDecimal getAmountByStep(int step) {
        BigDecimal result;
        switch (step) {
            default:
            case 0:
                result = new BigDecimal(0);// 0 always require
                break;
            case 1:
                result = new BigDecimal(50);
                break;
            case 2:
                result = new BigDecimal(500);
                break;
            case 3:
                result = new BigDecimal(5000);
                break;
            case 4:
                result = new BigDecimal(50000);
                break;
        }
        return result;
    }

    private int getStepFromLimit(long limit) {
        switch ((int) limit) {
            default:
            case 0:
                return 0;
            case 50:
                return 1;
            case 500:
                return 2;
            case 5000:
                return 3;
            case 50000:
                return 4;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public class LimitAdaptor extends RecyclerView.Adapter<LimitHolder> {

        private LayoutInflater inflater;
        private Handler handler = new Handler(Looper.getMainLooper());

        public LimitAdaptor(Context mContext) {
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public LimitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LimitHolder(inflater.inflate(R.layout.currency_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(LimitHolder holder, int position) {
            holder.itemView.setOnClickListener(v -> {
                Log.e(TAG, "onItemClick: " + position);
                BRKeyStore.putSpendLimit(items.get(position), SpendLimitActivity.this);
                handler.postDelayed(() -> adapter.notifyDataSetChanged(), 100);
            });
            FontManager.overrideFonts(holder.limit);
            int limit = items.get(position);
            String dgbLimit =  BRCurrency.getFormattedCurrencyString(SpendLimitActivity.this, "DGB", new BigDecimal(limit));
            holder.limit.setText(limit == 0 ? getString(R.string.no_limit) : dgbLimit);
            holder.check.setVisibility(
                    items.get(position) == BRKeyStore.getSpendLimit(SpendLimitActivity.this)  ? View.VISIBLE : View.GONE);
            holder.divider.setVisibility(position <= 2 ? View.VISIBLE : View.GONE);

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
package com.spawn.ai.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.spawn.ai.R;

public class SpawnChatLoadingViewHolder extends RecyclerView.ViewHolder {

    public LottieAnimationView loading;

    public SpawnChatLoadingViewHolder(View itemView) {
        super(itemView);

        loading = (LottieAnimationView) itemView.findViewById(R.id.bot_loading);

    }
}

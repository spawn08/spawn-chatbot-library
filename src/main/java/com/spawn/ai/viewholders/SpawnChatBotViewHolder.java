package com.spawn.ai.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.spawn.ai.R;

public class SpawnChatBotViewHolder extends RecyclerView.ViewHolder {

    public TextView bot_message, bot_time;

    public SpawnChatBotViewHolder(View itemView) {
        super(itemView);

        bot_message = itemView.findViewById(R.id.bot_message);
        bot_time = itemView.findViewById(R.id.bot_time);

    }
}

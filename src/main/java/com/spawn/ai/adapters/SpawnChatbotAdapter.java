package com.spawn.ai.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spawn.ai.R;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.viewholders.SpawnChatBotViewHolder;
import com.spawn.ai.viewholders.SpawnChatLoadingViewHolder;
import com.spawn.ai.viewholders.SpawnChatUserViewHolder;

import java.util.ArrayList;

import constants.ChatViewTypes;

public class SpawnChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ChatMessageType> chatMessageType = new ArrayList<>();
    IBotObserver iBotObserver;

    public SpawnChatbotAdapter(Context context, ArrayList<ChatMessageType> chatMessageType) {
        this.context = context;
        this.chatMessageType = chatMessageType;
        iBotObserver = (IBotObserver) context;
    }

    public void setAdapter(ArrayList<ChatMessageType> chatModels) {
        this.chatMessageType = chatModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatViewTypes.CHAT_VIEW_USER:
                View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_user, parent, false);
                SpawnChatUserViewHolder spawnChatViewHolder = new SpawnChatUserViewHolder(view);
                return spawnChatViewHolder;

            case ChatViewTypes.CHAT_VIEW_BOT:
                View viewBot = LayoutInflater.from(context).inflate(R.layout.spawn_chat_bot, parent, false);
                SpawnChatBotViewHolder spawnChatBotViewHolder = new SpawnChatBotViewHolder(viewBot);
                return spawnChatBotViewHolder;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                View viewLoading = LayoutInflater.from(context).inflate(R.layout.spawn_bot_loading, parent, false);
                SpawnChatLoadingViewHolder spawnChatLoadingViewHolder = new SpawnChatLoadingViewHolder(viewLoading);
                return spawnChatLoadingViewHolder;

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (chatMessageType.get(position).getViewType()) {
            case ChatViewTypes.CHAT_VIEW_USER:
                SpawnChatUserViewHolder spawnChatUserViewHolder = (SpawnChatUserViewHolder) holder;
                String message = chatMessageType.get(position).getMessage();
                String date = chatMessageType.get(position).getDate();
                spawnChatUserViewHolder.user_message.setText(message);
                spawnChatUserViewHolder.user_time.setText(date);

                break;
            case ChatViewTypes.CHAT_VIEW_BOT:

                SpawnChatBotViewHolder spawnChatBotViewHolder = (SpawnChatBotViewHolder) holder;
                String botMessage = chatMessageType.get(position).getMessage();
                String botDate = chatMessageType.get(position).getDate();
                spawnChatBotViewHolder.bot_message.setText(botMessage);
                spawnChatBotViewHolder.bot_time.setText(botDate);

                if (iBotObserver != null)
                    iBotObserver.speakBot(botMessage);
                break;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                SpawnChatLoadingViewHolder spawnChatLoadingViewHolder = (SpawnChatLoadingViewHolder) holder;
                spawnChatLoadingViewHolder.loading.setVisibility(View.VISIBLE);
                spawnChatLoadingViewHolder.loading.playAnimation();
                break;
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessageType.get(position).getViewType();
    }
}

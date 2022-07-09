package com.example.baseproject.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseproject.databinding.InfoAppItemBinding;
import com.example.baseproject.model.InfoApp;

public class InfoAppAdapter extends ListAdapter<InfoApp, InfoAppAdapter.InfoAppViewHolder> {

    protected InfoAppAdapter(@NonNull DiffUtil.ItemCallback<InfoApp> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public InfoAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InfoAppItemBinding itemBinding =
                InfoAppItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InfoAppViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAppViewHolder holder, int position) {
        InfoApp infoApp = getItem(position);
        if (infoApp != null) {
            holder.mBinding.setInfoApp(infoApp);
            holder.mBinding.executePendingBindings();
        }
    }

    public static class InfoAppViewHolder extends RecyclerView.ViewHolder {

        private final InfoAppItemBinding mBinding;

        public InfoAppViewHolder(@NonNull InfoAppItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

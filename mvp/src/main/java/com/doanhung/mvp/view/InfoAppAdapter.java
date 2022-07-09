package com.doanhung.mvp.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doanhung.mvp.databinding.InfoAppItemBinding;
import com.doanhung.mvp.model.InfoApp;


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
            holder.mBinding.imvAppIcon.setImageDrawable(infoApp.getAppIcon());
            holder.mBinding.tvAppName.setText(infoApp.getAppName());
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

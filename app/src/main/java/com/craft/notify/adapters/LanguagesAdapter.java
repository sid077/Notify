package com.craft.notify.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.craft.notify.R;
import com.craft.notify.models.VoicePojo;

import java.util.ArrayList;

public class LanguagesAdapter extends ArrayAdapter {
    int layout;
    ArrayList <VoicePojo> voices;
    public LanguagesAdapter(@NonNull Context context, int resource, ArrayList<VoicePojo> voices) {
        super(context, resource,voices);
        this.layout = resource;
        this.voices = voices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view ==null){
            view = LayoutInflater.from(getContext()).inflate(layout,null);

        }
        if(voices!=null){
            ImageView imageViewLock = view.findViewById(R.id.imageViewLock);
            if(voices.get(position).getLocked()){
                imageViewLock.setImageResource(R.drawable.ic_lock_black_24dp);
            }
            else {
                imageViewLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
            }
            TextView textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewTitle.setText(voices.get(position).getVoice().getLocale().getDisplayName()+"\n"+voices.get(position).getVoice().getName());
            LinearLayout linearLayoutNetwork = view.findViewById(R.id.linearLayoutNetwork);
            Log.d("network req", String.valueOf(voices.get(position).getVoice().isNetworkConnectionRequired()));
            if(voices.get(position).getVoice().isNetworkConnectionRequired()) {
                linearLayoutNetwork.setVisibility(View.VISIBLE);
            }else {
                linearLayoutNetwork.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }
}

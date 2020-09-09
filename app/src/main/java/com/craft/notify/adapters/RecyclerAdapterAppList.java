package com.craft.notify.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.craft.notify.activities.MainActivity;
import com.craft.notify.R;
import com.craft.notify.models.AppListPojo;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.UUID;

public class RecyclerAdapterAppList extends RecyclerView.Adapter<RecyclerAdapterAppList.ViewHolder> {


    List<AppListPojo> appListPojos;
    Context context;
    MainActivity mainActivity;
    public RecyclerAdapterAppList(List<AppListPojo> appListPojos ,MainActivity mainActivity) {
    this.appListPojos = appListPojos;
    this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RecyclerAdapterAppList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_app_list,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterAppList.ViewHolder holder, final int position) {

        if(appListPojos.get(position).getState()!=null&&appListPojos.get(position).getState()==1){
            holder.aSwitch.setChecked(true);
            mainActivity.getFirebaseAnalytics().setUserProperty("favapp"+ UUID.randomUUID(),appListPojos.get(position).getPackageName());
        }
        else {
            holder.aSwitch.setChecked(false);
        }
        String name = appListPojos.get(position).getPackageName();
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(name,0);
            holder.textViewAppName.setText(context.getPackageManager().getApplicationLabel(applicationInfo));
            holder.icon.setImageDrawable(context.getPackageManager().getApplicationIcon(name));
           // holder.icon.setImageResource(applicationInfo.icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int ind = name.lastIndexOf('.');



//        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                appListPojos.get(position).setState(isChecked?1:0);
//                mainActivity.updateAppInAppList(appListPojos.get(position));
//            }
//        });

    }

    @Override
    public int getItemCount() {
        try {
            return appListPojos.size();
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView textViewAppName;
        ImageView icon;
        SwitchMaterial aSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAppName = itemView.findViewById(R.id.textViewAppName);
            icon = itemView.findViewById(R.id.imageViewAppIcon);
            aSwitch = itemView.findViewById(R.id.switch1);
        }


    }
    public List<AppListPojo> getAppListPojos() {
        return appListPojos;
    }

    public void setAppListPojos(List<AppListPojo> appListPojos) {
        this.appListPojos = appListPojos;
    }
}

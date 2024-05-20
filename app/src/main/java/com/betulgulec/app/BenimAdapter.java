package com.betulgulec.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BenimAdapter extends RecyclerView.Adapter<BenimAdapter.IsletimSistemiViewHolder> {

    String veri1[], veri2[];
    AnasayfaFragment context;

    public BenimAdapter(AnasayfaFragment c, String[] s1, String[] s2) {
        context = c;
        veri1 = s1;
        veri2 = s2;
    }

    @NonNull
    @Override
    public BenimAdapter.IsletimSistemiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.benim_satir, parent, false);

        return new IsletimSistemiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BenimAdapter.IsletimSistemiViewHolder holder, int position) {
        holder.benimText1.setText(veri1[position]);
        holder.benimText2.setText(veri2[position]);
    }

    @Override
    public int getItemCount() {
        return veri1.length;
    }

    public class IsletimSistemiViewHolder extends RecyclerView.ViewHolder {
        TextView benimText1, benimText2;

        public IsletimSistemiViewHolder(@NonNull View itemView) {
            super(itemView);
            benimText1 = itemView.findViewById(R.id.yemek_txt);
            benimText2 = itemView.findViewById(R.id.yemek_calori_txt);
        }
    }
}

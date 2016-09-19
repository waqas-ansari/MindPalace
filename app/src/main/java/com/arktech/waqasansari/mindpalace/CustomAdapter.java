package com.arktech.waqasansari.mindpalace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by WaqasAhmed on 4/1/2016.
 */
public class CustomAdapter extends BaseAdapter implements SectionIndexer {
    private List<ClassMemoryDetail> memoryDetail;
    LayoutInflater inflater;
    Context context;

    HashMap<String, Integer> mapIndex;
    String[] sections;

    public CustomAdapter(Context context, List<ClassMemoryDetail> personDetail) {
        this.memoryDetail = personDetail;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Collections.sort(memoryDetail, new Comparator<ClassMemoryDetail>() {
            @Override
            public int compare(ClassMemoryDetail lhs, ClassMemoryDetail rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        //******************************************************************************************
        mapIndex = new LinkedHashMap<>();

        for (int x = 0; x < memoryDetail.size(); x++) {
            String fruit = memoryDetail.get(x).getTitle();
            String ch = fruit.substring(0, 1);
            ch = ch.toUpperCase(Locale.US);

            // HashMap will prevent duplicates
            mapIndex.put(ch, x);
        }

        Set<String> sectionLetters = mapIndex.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<>(sectionLetters);

        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
        //******************************************************************************************
    }

    @Override
    public int getCount() {
        return memoryDetail.size();
    }

    @Override
    public Object getItem(int position) {
        return memoryDetail.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.custom_list, null);
        }
        ClassMemoryDetail classMemoryDetail = memoryDetail.get(position);
        if(classMemoryDetail !=null){
            TextView txtPersonName = (TextView) convertView.findViewById(R.id.txtObject);
            TextView txtTags = (TextView) convertView.findViewById(R.id.txtTags);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            ImageView imgPerson = (ImageView) convertView.findViewById(R.id.imgEntry);

            txtPersonName.setText(classMemoryDetail.getTitle());
            txtTags.setText(classMemoryDetail.getTags());
            txtDate.setText(GetFormattedDate(classMemoryDetail.getDated()));
            txtDescription.setText(classMemoryDetail.getDescription());

            ColorGenerator generator = ColorGenerator.MATERIAL;

            int color = generator.getColor(classMemoryDetail.getTitle());

            String firstLetter = String.valueOf(classMemoryDetail.getTitle().charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
            imgPerson.setImageDrawable(drawable);
        }
        return convertView;
    }

    private String GetFormattedDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmma", Locale.getDefault());
        Date tempDate = null;
        Calendar temp = Calendar.getInstance();
        try {
            tempDate = dateFormat.parse(date);
            temp.setTime(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(tempDate);
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mapIndex.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}

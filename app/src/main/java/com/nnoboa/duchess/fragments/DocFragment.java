package com.nnoboa.duchess.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.PDFActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocFragment} factory method to
 * create an instance of this fragment.
 */
public class DocFragment extends Fragment {

    public DocFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_doc, container, false);

        FrameLayout openPdf = view.findViewById(R.id.open_pdf);
        FrameLayout openDoc = view.findViewById(R.id.open_doc);
        FrameLayout openXls = view.findViewById(R.id.open_xls);
        FrameLayout openPpt = view.findViewById(R.id.open_ppt);
//        FrameLayout openAll = view.findViewById(R.id.open_all);

        openPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","pdf");
                intent.putExtra("type1","pdf");
                intent.putExtra("type2","pdf");
                startActivity(intent);
            }
        });


        openDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","docx");
                intent.putExtra("type1","doc");
                intent.putExtra("type2","doc");

                startActivity(intent);
            }
        });

        openXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type",".xlsx");
                intent.putExtra("type1","csv");
                intent.putExtra("type2","xls");

                startActivity(intent);
            }
        });

        openPpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type",".pptx");
                intent.putExtra("type1","ppt");
                intent.putExtra("type2","odt");

                startActivity(intent);
            }
        });

//        openAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), PDFActivity.class);
//                intent.putExtra("type","all");
//                startActivity(intent);
//            }
//        });




        return view;
    }

}
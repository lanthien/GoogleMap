package com.example.thienlan.mymap.Code;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ThienLan on 27/01/2016.
 */
public class SaveLocation {

    Context context;

    public SaveLocation(Context context)
    {
        this.context= context;
    }

    public void WriteFile(String address)
    {
        String x = address+"###";
        try
        {
            if(address!=null) {
                FileOutputStream fout = context.openFileOutput("location.txt", Context.MODE_APPEND);
                fout.write(x.getBytes());
            }

        }
        catch (Exception e)
        {
            System.out.println("Exception"+e.getMessage());
        }
    }

    public String[] LoadFile()
    {
        String[] x;
        try {
            FileInputStream fin = context.openFileInput("location.txt");
            byte[] buffer = new byte[fin.available()];
            fin.read(buffer);
            String chuoi = new String(buffer);
            x=chuoi.split("###");
            return x;
        }
        catch (Exception e)
        {
            System.out.println("Exception"+e.getMessage());
            return null;
        }
    }
}

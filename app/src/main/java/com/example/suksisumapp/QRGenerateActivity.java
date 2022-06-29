package com.example.suksisumapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QRGenerateActivity extends AppCompatActivity {

    Button BtnQRDate, BtnQRStartTime, BtnQREndTime, BtnGenerate, BtnQRSave;
    int Hour, Minute;
    TextView QRTitle;
    ImageView IVOutput;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);

        BtnQRDate = findViewById(R.id.BtnQRDate);
        BtnQRStartTime = findViewById(R.id.BtnQRStartTime);
        BtnQREndTime = findViewById(R.id.BtnQREndTime);
        BtnGenerate = findViewById(R.id.BtnGenerate);

        ActivityCompat.requestPermissions(QRGenerateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(QRGenerateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        BtnQRDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                        BtnQRDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(QRGenerateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        BtnQRStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(QRGenerateActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour = hourOfDay;
                                Minute = minute;

                                String time = Hour + ":" + Minute;

                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "HH:mm"
                                    );
                                    BtnQRStartTime.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(Hour, Minute);
                timePickerDialog.show();
            }
        });

        BtnQREndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(QRGenerateActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour = hourOfDay;
                                Minute = minute;

                                String time = Hour + ":" + Minute;

                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "HH:mm"
                                    );
                                    BtnQREndTime.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(Hour, Minute);
                timePickerDialog.show();
            }
        });

        BtnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize multi format writer
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    String text1 = BtnQRDate.getText().toString().trim();
                    String text2 = BtnQRStartTime.getText().toString().trim();
                    String text3 = BtnQREndTime.getText().toString().trim();
                    //Get input value from edit text
                    String textQR = text1 + " (" + text2 + "-" + text3 + ")";
                    //Initialize bit matrix
                    BitMatrix matrix = writer.encode(textQR, BarcodeFormat.QR_CODE, 350, 350);
                    //Initialize barcode encoder
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    //Initialize bitmap
                    Bitmap bitmap = encoder.createBitmap(matrix);

                    showImageDialog(textQR, bitmap);

                    //Set bitmap on image view
                    //IVOutput.setImageBitmap(bitmap);
                    //Initialize input manager
                    InputMethodManager manager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    //Hide soft keyboard
                    //manager.hideSoftInputFromWindow(ETGenerate.getApplicationWindowToken(), 0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showImageDialog(final String textQR, Bitmap bitmap) {
        //Show dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.qr_image_dialog, null);
        dialogBuilder.setView(dialogView);

        relativeLayout = dialogView.findViewById(R.id.ImageLayout);
        QRTitle = dialogView.findViewById(R.id.QRTitle);
        IVOutput = dialogView.findViewById(R.id.IVOutput);
        BtnQRSave = dialogView.findViewById(R.id.BtnQRSave);

        QRTitle.setText(textQR);

        //Set bitmap on image view
        IVOutput.setImageBitmap(bitmap);

        BtnQRSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void saveToGallery() {

        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        relativeLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = relativeLayout.getDrawingCache();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Download");
        dir.mkdirs();

        String filename = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, filename);

        if (outFile.exists()){
            outFile.delete();
        }

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "Image saved in file", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "Error: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
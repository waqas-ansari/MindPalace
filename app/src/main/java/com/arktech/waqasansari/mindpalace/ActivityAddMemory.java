package com.arktech.waqasansari.mindpalace;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAddMemory extends AppCompatActivity {
    FloatingActionButton btnAddPhoto;

    LinearLayout imgLinearLayout;

    Spinner spnType;
    EditText edtTitle;
    EditText edtDescription;
    EditText edtDate;

    boolean isEdit = false;

    String dateAndTime = null;

    List<String> imagesList;

    DatabaseHandler handler;
    ClassMemoryDetail memory;

    Uri imageUri = null;

    View tempView;

    String currentPhoto;

    ChipsMultiAutoCompleteTextview mu;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int FILECHOOSER_RESULTCODE = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imagesList = new ArrayList<>();


        spnType = (Spinner) findViewById(R.id.edtType);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        edtDate = (EditText) findViewById(R.id.edtDate);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spnType.getItemAtPosition(position).equals("Select Type")){
                    spnType.setBackgroundResource(R.drawable.bg_error);
                    return;
                }
                spnType.setBackgroundResource(R.drawable.shape_drop_down_spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mu = (ChipsMultiAutoCompleteTextview) findViewById(R.id.edtTags);
        if (mu != null) {
            mu.setImeActionLabel("Add", EditorInfo.IME_ACTION_DONE);
        }
        mu.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(mu.getText().charAt(mu.getText().length()-1) != ' ')
                        mu.append(" ");
                    else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

        imgLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        btnAddPhoto = (FloatingActionButton) findViewById(R.id.btnAddImages);

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture(v);
            }
        });


        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateAndTime = new SimpleDateFormat("ddMMyyyyhhmma", Locale.getDefault()).format(myCalendar.getTime());
                updateLabel(myCalendar);
            }

        };


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityAddMemory.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Intent intent = getIntent();
        if(intent.getExtras() != null){
            isEdit = true;
            ProgressDialog dialog = new ProgressDialog(ActivityAddMemory.this);
            dialog.setTitle("Please wait...");
            dialog.setMessage("Getting data.");
            dialog.show();
            int posOfType=0;
            memory = (ClassMemoryDetail) intent.getSerializableExtra("memoryObject");
            switch (memory.getType()){
                case "Person":
                    posOfType = 1;
                    break;
                case "Event":
                    posOfType = 2;
                    break;
                case "Visit":
                    posOfType = 3;
                    break;
                case "Object":
                    posOfType = 4;
                    break;
                case "Other":
                    posOfType = 5;
                    break;
            }
            spnType.setSelection(posOfType);

            edtTitle.setText(memory.getTitle());
            edtDescription.setText(memory.getDescription());
            for(int i=0; i<memory.getTags().length(); i++){
                mu.append(String.valueOf(memory.getTags().charAt(i)));
            }

            dateAndTime = memory.getDated();
            edtDate.setText(Utility.getFormattedDate(memory.getDated()));
            String[] images;
            if(memory.getImage() != null){
                images = memory.getImage().trim().split(",");
                for(int i=0; i<images.length; i++){
                    imagesList.add(images[i]);
                    imgLinearLayout.addView(addImageView(Utility.decodeSampledBitmap(imagesList.get(i), 50, 50)));
                }
            }

            if(imagesList.size() == 4)
                btnAddPhoto.setVisibility(View.GONE);

            if(imagesList.size() > 0)
                if(imgLinearLayout.getVisibility() == View.GONE) {
                    imgLinearLayout.setVisibility(View.VISIBLE);
                }

            dialog.dismiss();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK) {
            if(imgLinearLayout.getVisibility() == View.GONE) {
                imgLinearLayout.setVisibility(View.VISIBLE);
            }

            String path=null;
            Uri uri;
            if (intent == null || intent.getData() == null)
                uri = this.imageUri;
            else
                uri = intent.getData();

            if(requestCode == SELECT_FILE) {
                path = getRealPathFromURI(uri);
            } else if(requestCode == REQUEST_CAMERA){
                path = uri.getEncodedPath();
            }


            if(tempView.getClass().getName().equalsIgnoreCase("android.widget.ImageView") && imagesList.size() > 1) {
                ((ImageView) tempView).setImageBitmap(Utility.decodeSampledBitmap(path, 50, 50));
                imagesList.set(tempView.getId(), path);
                return;
            }

            imagesList.add(path);


            if(imgLinearLayout != null) {
                ImageView imgToBeAdded = addImageView(Utility.decodeSampledBitmap(path, 50, 50));
                imgLinearLayout.addView(imgToBeAdded);
            }

        }


        if(imagesList.size() == 4)
            btnAddPhoto.setVisibility(View.GONE);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_memory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_memory){
            if(checkFields() && dateAndTime != null){
                try {
                    if(!isEdit)
                        memory = new ClassMemoryDetail();
                    handler = new DatabaseHandler(ActivityAddMemory.this);
                    memory.setType(spnType.getSelectedItem().toString());
                    memory.setTitle(edtTitle.getText().toString());
                    memory.setDescription(edtDescription.getText().toString());
                    memory.setDated(dateAndTime);

                    if (mu.getText().toString().charAt(mu.length() - 1) != ' ')
                        mu.append(" ");
                    memory.setTags(mu.getText().toString());

                    if(imagesList.size() == 0){
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityAddMemory.this);
                        alertDialog.setTitle("Select Image")
                                .setMessage("Do you want to continue without image?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        memory.setImage(null);
                                        updateAndStartActivity(isEdit);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    } else {
                        try {
                            memory.setImage(makeImageString(copyFileAndGetDestinations()));
                            updateAndStartActivity(isEdit);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else Toast.makeText(getApplicationContext(), "Don't leave incomplete form", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(imagesList.size() > 0 || !edtTitle.getText().toString().equals("") || !edtDescription.getText().toString().equals("") || !edtDate.getText().toString().equals("") || !mu.getText().toString().equals("")){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityAddMemory.this);
            if(isEdit)
                alertDialog.setMessage("Do you want to quit editing this memory?");
            else alertDialog.setMessage("Do you want to quit adding this memory?");
            alertDialog.setTitle("Warning!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else super.onBackPressed();
    }

















































































    //***********************************************************************************************

    private void showCamera() {

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DCIM");
            if (!file.exists()) {
                file.mkdirs();
            }

            File localFile2 = new File(file + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            imageUri = Uri.fromFile(localFile2);

            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                cameraIntent.setClipData(ClipData.newRawUri(null, Uri.fromFile(localFile2)));
            }

            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } catch (Exception localException) {
            Toast.makeText(ActivityAddMemory.this, "Exception:" + localException, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPicture(View view) {
        tempView = view;
        final CharSequence[] items;
        if(view.getClass().getName().equalsIgnoreCase("android.widget.ImageView"))
                items = new CharSequence[]{ "Take Photo", "Choose from Library", "Remove Photo", "Cancel" };
        else items = new CharSequence[]{ "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddMemory.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    showCamera();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if(items[item].equals("Remove Photo")) {
                    imgLinearLayout.removeView(tempView);
                    imagesList.remove(tempView.getId());
                    if(imagesList.size() < 1) {
                        imgLinearLayout.setVisibility(View.GONE);
                    }

                    if(btnAddPhoto.getVisibility() == View.GONE)
                        btnAddPhoto.setVisibility(View.VISIBLE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        if(data.getData() == null)
            data.setData(Uri.fromFile(destination));

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumbnail;
    }

    private Bitmap onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        return bm;
    }

    private void updateLabel(Calendar myCalendar) {
        SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        edtDate.setText(newFormat.format(myCalendar.getTime()));

    }

    private boolean checkFields() {
        if(spnType.getSelectedItem().toString().isEmpty() || spnType.getSelectedItem().toString().equals("Select Type")) {
            spnType.setBackgroundResource(R.drawable.bg_error);
            return false;
        }
        if(edtTitle.getText().toString().isEmpty()){
            edtTitle.setError("Title is missing");
            return false;
        }
        if(edtDescription.getText().toString().isEmpty()){
            edtDescription.setError("Give some description");
            return false;
        }
        if(mu.getText().toString().isEmpty()){
            mu.setError("Enter some tags");
            return false;
        }
        if(edtDate.getText().toString().isEmpty()){
            edtDate.setError("Date is necessary.");
            return false;
        }
        return true;
    }


    private ImageView addImageView(Bitmap bitmap){
        ImageView imgToBeAdded = new ImageView(getApplicationContext());

        int dpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        int dpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpWidth,dpHeight);
        layoutParams.setMargins(0,margin,margin,margin);

        imgToBeAdded.setId(imagesList.size() - 1);
        imgToBeAdded.setLayoutParams(layoutParams);
        imgToBeAdded.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgToBeAdded.setImageBitmap(bitmap);

        imgToBeAdded.setClickable(true);
        imgToBeAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture(v);
            }
        });
        return imgToBeAdded;
    }

    private void copyFile(File source, File destination) throws IOException {
        if(! source.exists())
            return;

        FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel destinationChannel = new FileOutputStream(destination).getChannel();

        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        sourceChannel.close();
        destinationChannel.close();
    }

    private String makeImageString(String[] imagePaths){
        StringBuilder builder = new StringBuilder();
        for(String paths : imagePaths){
            builder.append(paths).append(",");
        }
        return builder.toString();
    }

    public String getRealPathFromURI(Uri uri){
        String filePath = "";
        String[] filePahColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePahColumn, null, null, null);
        if (cursor != null) {
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePahColumn[0]);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    private String[] copyFileAndGetDestinations() throws IOException {
        String str1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        File temp = new File(str1 + "/.MindPalace/Media/Images" + File.separator);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        String[] arrayOfString = new String[imagesList.size()];
        for (int i = 0; i < imagesList.size(); i++)
        {
            if(isEdit)
                if(imagesList.get(i).contains("MindPalace")){
                    arrayOfString[i] = imagesList.get(i);
                    continue;
                }
            File source = new File(this.imagesList.get(i));
            File destination = new File(temp, System.currentTimeMillis() + ".jpg");
            arrayOfString[i] = destination.toString();
            destination.createNewFile();
            copyFile(source, destination);
        }
        return arrayOfString;
    }

    private void updateAndStartActivity(boolean isEdit){
        if(isEdit) {
            handler.updateDetail(memory);
            Utility.isMemoryEdited = true;
        }
        else handler.addDetail(memory);
        Toast.makeText(getApplicationContext(), "Saved Successfully.", Toast.LENGTH_SHORT).show();
        finish();
        Intent localIntent = new Intent(getApplicationContext(), ActivityMemoryDetail.class);
        localIntent.putExtra("memoryObject", memory);
        startActivity(localIntent);
    }

    //***********************************************************************************************


}

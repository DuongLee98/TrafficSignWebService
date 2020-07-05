package com.kl.cameraapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.kl.cameraapp.R;
import com.kl.cameraapp.controller.MyService;
import com.kl.cameraapp.data.remote.RetrofitClient;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FragmentCamera extends Fragment {
    Button buttonCapture, btnSave;
    ImageButton btnUpload;
    ImageView imageView;
    String currentPhotoPath = "";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    PopupWindow popupWindow;
    static String baseURL = "http://192.168.0.107:5000";
    public FragmentCamera(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        buttonCapture = v.findViewById(R.id.buttonCapture);
        btnUpload = v.findViewById(R.id.btn_upload);
        imageView = v.findViewById(R.id.imageView);

        btnSave = v.findViewById(R.id.button_save);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); // bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    String base64Img = Base64.encodeToString(b, Base64.DEFAULT);
                    JSONObject data = new JSONObject();
                    data.put("img", base64Img);
//                    Log.i("json", data.toString());
//                    TrafficService ts = new TrafficService("http://192.168.0.107:5000/api/v1/trafficsign", data);
//                    ts.execute();
//                    Toast.makeText(getBaseContext(), "gelo", Toast.LENGTH_LONG).show();
//                    String jsonStr = "{'img':" +base64Img+"}";

                    MyService myRetrofit = RetrofitClient.getInstance(baseURL).create(MyService.class);

                    Call<JsonObject> homeCallBack = myRetrofit.postImage(data);
                    homeCallBack.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                            Log.d("zzz", response.body().get("img").getAsString());
                            LayoutInflater inflater = (LayoutInflater)
                                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View popupView = inflater.inflate(R.layout.popup_result, null);

                            // create the popup window
                            int width = LinearLayout.LayoutParams.MATCH_PARENT;
                            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            boolean focusable = true; // lets taps outside the popup also dismiss it
                            popupWindow = new PopupWindow(popupView, width, height, focusable);
                            popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);
                            JsonObject info = new JsonObject();

//                            Log.d("img info", info);
                            ImageView imgv = popupView.findViewById(R.id.img_res);
                            Bitmap res = convertBase64ToBitmap(response.body().get("img").getAsString());
                            imgv.setImageBitmap(res);
                            Button btnDOne, btnSave;
                            btnDOne = popupView.findViewById(R.id.btn_done);
                            btnDOne.setOnClickListener(v1 -> popupWindow.dismiss());
                            btnSave = popupView.findViewById(R.id.btn_save_img_res);
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("zzz", "fail", t);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imgFile = new  File("/DCIM/CAMERA/rsz2.jpg");

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    currentPhotoPath = "/DCIM/CAMERA/rsz2.jpg";
                }
            }
        });
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        return v;
    }
    private Bitmap convertBase64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private void galleryAddPic() {
        try {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getContext().sendBroadcast(mediaScanIntent);
            Toast.makeText(getContext(), "Saved photo", Toast.LENGTH_LONG).show();

        } catch (Exception e) {

        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
                Glide.with(getContext()).load(currentPhotoPath).into(imageView);

        }

    }
}

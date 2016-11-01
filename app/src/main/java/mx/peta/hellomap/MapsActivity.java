package mx.peta.hellomap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import mx.peta.hellomap.servicios.ServicioGPS;

import static android.R.attr.bitmap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private Button btnFin;

    File photoFile = null;

    int count = 0;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ServicioGPS servicioGPS = new ServicioGPS(getApplicationContext());
        TextView t = (TextView) findViewById(R.id.textoUbicacion);
        servicioGPS.setView(t);

        btnFin = (Button) findViewById(R.id.btnFin);
        btnFin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setClickable(true);

        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Image Touch", Toast.LENGTH_LONG).show();
            }
        });

        //mImageView.setImageURI(Uri.fromFile(testCreateImageFile()));
        String path = testCreateImageFile().getAbsolutePath();
        Bitmap bitmap=null;
        //File f= new File(path);
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        //try {
        //    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        //}
        //mImageView.setImageBitmap(bitmap);
        bitmap = BitmapFactory.decodeFile(path);
        int bAlto = bitmap.getHeight();
        int bAncho = bitmap.getWidth();

        float scale = Math.min((float) 150.0 / (float) bAlto, (float) 150.0 / (float) bAncho);
        Bitmap chico = Bitmap.createScaledBitmap(bitmap,(int)(bAncho*scale), (int)(bAlto*scale), true);
        mImageView.setImageBitmap(chico);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Se verifica si hay una aplicacion que pueda tomar la foto
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // se crea el archivo donde se guardara la foto
            photoFile = createImageFile();

            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                //Toast.makeText(getApplicationContext(),  photoURI.toString(),Toast.LENGTH_LONG).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            ++count;
            Toast.makeText(getApplicationContext(), "Count " + count, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Count " + count, Toast.LENGTH_LONG).show();
            // startActivityForResult(takePictureIntent, 100);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //mMap.setMyLocationEnabled(true);
        LatLng mexico = new LatLng(19.341822116645, -99.183682);
        mMap.addMarker(new MarkerOptions().title("México").position(mexico).snippet("Marker in México"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico, 13));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mImageView.setImageURI(Uri.fromFile(photoFile));
        }
    }

    private File createImageFile()  {
        // Create an image file name
        // the image will be stored in the DCIM directory
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getDefault());
        long dateTaken = calendar.getTimeInMillis();

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
        if (!dir.exists()) dir.mkdirs();
        final File photoFile = new File(dir, DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString() + ".jpg");

        return photoFile;
    }

    private File testCreateImageFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
        if (!dir.exists()) dir.mkdirs();
        final File photoFile = new File("/storage/emulated/0/DCIM/Camera/", "20161031_151205.jpg");

        return photoFile;
    }
}

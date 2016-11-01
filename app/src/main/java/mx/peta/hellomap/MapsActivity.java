package mx.peta.hellomap;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import mx.peta.hellomap.servicios.ServicioGPS;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView mImageView;
    private Button btnFin;

    File photoFile = null;

    int count = 0;
    String bitmapFileName = null;
    String carpetaPropiedades;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // creamos la carpeta en donde vamos a guardar los thumbnail
        carpetaPropiedades = verificarCrearCarpeta("propiedades");

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

        /*
        String path = testCreateImageFile().getAbsolutePath();
        Bitmap bitmap;

        bitmap = BitmapFactory.decodeFile(path);
        int bAlto = bitmap.getHeight();
        int bAncho = bitmap.getWidth();

        float scale = Math.min((float) 150.0 / (float) bAlto, (float) 150.0 / (float) bAncho);
        Bitmap chico = Bitmap.createScaledBitmap(bitmap,(int)(bAncho*scale), (int)(bAlto*scale), true);
        mImageView.setImageBitmap(chico);
        */

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Se verifica si hay una aplicacion que pueda tomar la foto
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // se crea el archivo donde se guardara la foto
            //photoFile = createImageFile();

            //if (photoFile != null) {
            //    Uri photoURI = Uri.fromFile(photoFile);
            //Toast.makeText(getApplicationContext(),  photoURI.toString(),Toast.LENGTH_LONG).show();
            //    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //}
            ++count;
            System.out.println("Inmobilia mainActivity.onCreate count = " + count);
            startActivityForResult(takePictureIntent, 100);
        }

        // creamos la carpeta en donde vamos a guardar los thumbnail

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
        mMap.addMarker(new MarkerOptions().title("México").position(mexico).snippet("Mi Ciudad"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico, 13));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Inmobilia onActivity result");
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Hay que salvar el bitmap a disco
            File savedBitmap = createImageFile(carpetaPropiedades);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(savedBitmap);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                System.out.println("Inmobilia error while writing a image to disk");
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setPic(savedBitmap.toString(), mImageView);

            // Hay que almacenar el nombre del archivo en SQLite junto con los datos del inmueble

            //String path = photoFile.getAbsolutePath();
            //Bitmap bitmap;

            //bitmap = BitmapFactory.decodeFile(path);
            //int bAlto = bitmap.getHeight();
            //int bAncho = bitmap.getWidth();

            //float scale = Math.min((float) 150.0 / (float) bAlto, (float) 150.0 / (float) bAncho);
            //Bitmap chico = Bitmap.createScaledBitmap(bitmap,(int)(bAncho*scale), (int)(bAlto*scale), true);
            //mImageView.setImageBitmap(chico);

        }
    }

    private File createImageFile(String filePath) {  // Crea el path unico para almacenar una imagen
        // Create an image file name
        // the image will be stored in the DCIM directory
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getDefault());
        long dateTaken = calendar.getTimeInMillis();

        // File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
        File dir = new File(filePath + "/");
        final File photoFile = new File(dir, DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString() + ".jpg");

        return photoFile;
    }

    private File testCreateImageFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
        if (!dir.exists()) dir.mkdirs();
        final File photoFile = new File("/storage/emulated/0/DCIM/Camera/", "20161031_151205.jpg");

        return photoFile;
    }

    private String verificarCrearCarpeta(String dirname) {
        String dir = "unknown";
        PackageManager m = getPackageManager();
        String packageName = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(packageName, 0);
            dir = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println(packageName + " Error Package name not found ");
            return null;
        }

        System.out.println("Inmobilia app directory " + dir);
        dir = dir + "/" + dirname;
        System.out.println("Inmobilia property directory " + dir);
        File f = new File(dir);

        // Comprobamos si la carpeta está ya creada
        // Si la carpeta no está creada, la creamos.
        if(!f.isDirectory()) {
            System.out.println("Inmobilia creating directory");
            File myNewFolder = new File(dir);
            myNewFolder.mkdir(); //creamos la carpeta
        }
        if (f.isDirectory()) {
            System.out.println("Inmobilia directory exist");
        }
        return dir;
    }

    private void setPic(String mCurrentPhotoFilePath, ImageView mImageView) {
        // Get the dimensions of the View
        Display display = getWindowManager().getDefaultDisplay();  // Recuperamos las características del display
        Point size = new Point();;
        display.getSize(size); // size contiene el tamaño del display en pixels
        int imageSize = (int) (Math.min(size.x,size.y) / 3); // el tamaño de la imagen sera una tercera parte del lado mas corto

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/imageSize, photoH/imageSize);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoFilePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}

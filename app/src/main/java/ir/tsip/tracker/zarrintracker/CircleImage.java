package ir.tsip.tracker.zarrintracker;


        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.graphics.PorterDuff.Mode;
        import android.graphics.PorterDuffXfermode;
        import android.graphics.Rect;



public class CircleImage {

//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//                 super.onCreate(savedInstanceState);\nsetRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        setContentView(R.layout.circle_layout);
//        ImageView img1 = (ImageView) findViewById(R.id.imageView1);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(),
//                R.drawable.hair_four);
//        Bitmap resized = Bitmap.createScaledBitmap(bm, 100, 100, true);
//        Bitmap conv_bm = getRoundedRectBitmap(resized, 100);
//        img1.setImageBitmap(conv_bm);
//    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, pixels, pixels);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(pixels / 2, pixels / 2 , pixels / 2 , paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

}
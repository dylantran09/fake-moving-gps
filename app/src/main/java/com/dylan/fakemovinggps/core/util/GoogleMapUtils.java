package com.dylan.fakemovinggps.core.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

/**
 * @author Tyrael
 * @version 1.0 <br>
 * @since October 2015
 */

@SuppressWarnings("unused")
public final class GoogleMapUtils {

    private static final float COLOR_RANGE = 120f;
    private static double maxSpeed;
    private static double minSpeed;

    private static double getMaxSpeed(List<Location> location) {
        double speed = 0;

        for (Location position : location) {
            if (position.getSpeed() > speed) {
                speed = position.getSpeed();
            }
        }

        return speed;
    }

    private static double getMinSpeed(List<Location> location) {
        if (location.size() > 0) {
            if (location.get(0) != null) {
                double speed = location.get(0).getSpeed();

                for (Location position : location) {
                    if (position.getSpeed() < speed) {
                        speed = position.getSpeed();
                    }
                }
                return speed;
            }
        }

        return 0;
    }

    private static int getColorBySpeed(double speed) {
        double total = maxSpeed - minSpeed;
        double number = speed - minSpeed;
        float H = (float) ((number / total) * COLOR_RANGE);
        float S = 0.9f;
        float V = 0.9f;
        if (total == 0) {
            H = 0;
        }
        if (H < 0) {
            H = 0;
        } else if (H > COLOR_RANGE) {
            H = COLOR_RANGE;
        }
        return Color.HSVToColor(new float[]{H, S, V});
    }

    private static Bitmap createRoute(GoogleMap map, int width, int height, List<Location> location) {
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(2));
        paint.setAntiAlias(true);

        int startColor = getColorBySpeed(location.get(0).getSpeed());
        int endColor;

        Bitmap routeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas routeCanvas = new Canvas(routeBitmap);

        for (int i = 0; i < location.size() - 1; i++) {
            Point startPoint = map.getProjection().toScreenLocation(new LatLng(location.get(i).getLatitude(), location.get(i).getLongitude()));
            Point endPoint = map.getProjection().toScreenLocation(new LatLng(location.get(i + 1).getLatitude(), location.get(i + 1).getLongitude()));

            endColor = getColorBySpeed(location.get(i + 1).getSpeed());

            Shader gradientShader = new LinearGradient(startPoint.x, startPoint.y, endPoint.x, endPoint.y, startColor, endColor, Shader.TileMode.MIRROR);
            paint.setShader(gradientShader);

            startColor = endColor;

            routeCanvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
        }

        return routeBitmap;
    }


    public static void drawRouteOnMap(GoogleMap map, List<Location> location, int containerWidth, int containerHeight) {
        Bitmap bitmap = createRoute(map, containerWidth, containerHeight, location);
        GroundOverlayOptions overlay = new GroundOverlayOptions();
        overlay.image(BitmapDescriptorFactory.fromBitmap(bitmap));
        overlay.positionFromBounds(map.getProjection().getVisibleRegion().latLngBounds);
        map.addGroundOverlay(overlay);
        zoomToRoute(map, location);
    }

    public static void zoomToRoute(GoogleMap map, List<Location> location) {
        if (location != null && location.size() > 0) {
            Location farthestPoint = location.get(0);
            Location startPoint = location.get(0);
            for (Location end : location)
                farthestPoint = getFarthestPoint(startPoint, end, farthestPoint);

            double[] middlePoint = Utils.getMiddleLocation(startPoint.getLatitude(), startPoint.getLongitude(),
                    farthestPoint.getLatitude(), farthestPoint.getLongitude());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLngBounds bound = builder.include(new LatLng(startPoint.getLatitude(), startPoint.getLongitude())).include(new LatLng(farthestPoint.getLatitude(), farthestPoint.getLongitude())).build();
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(middlePoint[0], middlePoint[1])));
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 20));
        }
    }

    private static Location getFarthestPoint(Location start, Location end, Location last) {
        return (Utils.calculateDistance(start.getLongitude(), start.getLatitude(), end.getLongitude(), end.getLatitude()) >= Utils.calculateDistance(start.getLongitude(), start.getLatitude(), last.getLongitude(), last.getLatitude())) ? end : last;
    }

}

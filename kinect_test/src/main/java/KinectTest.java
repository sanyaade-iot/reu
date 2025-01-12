import org.openkinect.freenect.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class KinectTest {
    public static void main(String[] args) throws InterruptedException {
        final ImageUI ui = new ImageUI();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ui.show();
            }
        });
        t.start();

        // DECLARATIONS
        Context ctx = null;
        Device dev = null;

        // INITIALIZE DEVICE
        ctx = Freenect.createContext();
        if (ctx.numDevices() > 0) {
            dev = ctx.openDevice(0);
        } else {
            System.err.println("No kinects detected.  Exiting.");
            System.exit(0);
        }

        // TILT UP, DOWN, & RETURN
        //dev.setTiltAngle(20);
        //Thread.sleep(4000);
        //dev.setTiltAngle(-20);
        //Thread.sleep(4000);
        //dev.setTiltAngle(0);

        dev.startVideo(new VideoHandler() {
            @Override
            public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                BufferedImage im = new BufferedImage(mode.getWidth(), mode.getHeight(), BufferedImage.TYPE_INT_RGB);
                for(int y=0; y<mode.height; y++) {
                    for(int x=0; x<mode.width; x++) {
                        int offset = 3*(y*mode.width+x);

                        int r = frame.get( offset+2 ) & 0xFF;
                        int g = frame.get( offset+1 ) & 0xFF;
                        int b = frame.get( offset+0 ) & 0xFF;

                        int pixel = (0xFF) << 24
                                | (b & 0xFF) << 16
                                | (g & 0xFF) << 8
                                | (r & 0xFF) << 0;
                        im.setRGB(x, y, pixel);
                    }
                }
                ui.setImage(im);
                ui.repaint();
            }
        });
        Thread.sleep(1000000);

        // SHUT DOWN
        if (ctx != null) {
            if (dev != null) {
                dev.close();
            }
        }
        ctx.shutdown();
    }
}

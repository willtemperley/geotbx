package gtbx;

import it.jrc.GenericReaderPlugIn;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by willtemperley@gmail.com on 08-Feb-16.
 */
public class PngWriter {

    public void getPng(RenderedImage renderedImage) throws IOException {

        Raster data = renderedImage.getData();

        int tileSize = 256;

        int[] intArrayOut = new int[tileSize * tileSize];

        int opaque = 0xFF000000;
        for (int i = 0; i < (tileSize * tileSize); i++) {
//            int red = productData2.getElemIntAt(i) / 100;
//            int green = productData3.getElemIntAt(i) / 100;
//            int blue = productData4.getElemIntAt(i) / 100;
            int red = 255, green = 255, blue = 255;
            intArrayOut[i] = opaque | (red << 16) | (green << 8) | blue;
        }

        BufferedImage img = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, tileSize, tileSize, intArrayOut, 0, 1000);

//        BufferedImage image = ras(tileSize, intArrayOut);//new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);

        OutputStream outputStream = new FileOutputStream(new File("E:/tmp/aaa.png"));
        ImageIO.write(img, "png", outputStream);

    }
}
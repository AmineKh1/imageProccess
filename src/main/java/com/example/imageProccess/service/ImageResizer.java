package com.example.imageProccess.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.HashSet;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Service
public class ImageResizer {

    public static File resize(String inputImagePath,
                              String outputImagePath,
                              int scaledWidth,
                              int scaledHeight)
            throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
//        BufferedImage outputImage = new BufferedImage(scaledWidth,
//                scaledHeight, inputImage.getType());

      var  outputImage =  Scalr.resize(inputImage, scaledWidth, scaledHeight,null);
        // scales the input image to the output image
//        Graphics2D g2d = outputImage.createGraphics();
//
//        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
//        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        File output = new File(outputImagePath);
     //   return ImageIO.createImageOutputStream(outputImage);
        if ( ImageIO.write(outputImage, formatName, output))
            return output;
        return null;
    }

    public static File resize(String inputImagePath,
                              String outputImagePath,
                              double percent) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
      return   resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }

    public Set<File> resizeImage(File file,
                                 String outputImagePath){

        String basename = file.getName().split("\\.")[0];
        String ext = file.getName().split("\\.")[1];
        String outputImagePath1 = String.format("%s/%s%s.%s",outputImagePath ,basename , "_medium",ext);
        String outputImagePath2 = String.format("%s/%s%s.%s",outputImagePath ,basename , "_small",ext);
        String outputImagePath3 = String.format("%s/%s%s.%s",outputImagePath ,basename , "_large",ext);


        try {
          var set = new HashSet<File>();
//            int scaledWidth = 1024;
//            int scaledHeight = 768;
           set.add( ImageResizer.resize(file.getPath(), outputImagePath1,1));
            double percent = 0.5;
            set.add(ImageResizer.resize(file.getPath(), outputImagePath2, percent));
            percent = 1.5;
            set.add(ImageResizer.resize(file.getPath(), outputImagePath3, percent));
            return set;
        } catch (IOException ex) {
            System.out.println("Error resizing the image.");
            ex.printStackTrace();
        }
        return null;
    }


}

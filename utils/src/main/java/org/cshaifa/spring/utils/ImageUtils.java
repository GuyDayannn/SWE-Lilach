package org.cshaifa.spring.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

public class ImageUtils {

    public static <T> List<Path> getAllImagesFromFolder(String path, Class<T> c) {
        List<Path> imagesList = new ArrayList<>();
        FileSystem fileSystem = null;

        try {
            Path imagesPath;
            URI imagesUri = c.getResource(path).toURI();

            if (imagesUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(imagesUri, Collections.emptyMap(), null);
                imagesPath = fileSystem.getPath(imagesUri.toString().split("!")[1]);
            } else {
                imagesPath = Paths.get(imagesUri);
            }

            for (Iterator<Path> it = Files.walk(imagesPath, 1).iterator(); it.hasNext();) {
                Path folderPath = it.next().toAbsolutePath();
                if (!Files.isDirectory(folderPath))
                    imagesList.add(folderPath.toAbsolutePath());
            }

            if (fileSystem != null)
                fileSystem.close();
        } catch (Exception e) {
            // TODO: maybe report this somewhere
            e.printStackTrace();
        }

        return imagesList;
    }

    private static byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    public static byte[] getByteArrayFromURI(URI imageUri) throws IOException {
        if (imageUri.getScheme().equals("jar"))
            return null;

        BufferedImage image = ImageIO.read(new File(imageUri));

        return bufferedImageToByteArray(image);
    }

    public static <T> byte[] getByteArrayFromURI(URI imageUri, Class<T> c) throws IOException {
        BufferedImage image;

        if (imageUri.getScheme().equals("jar"))
            image = ImageIO.read(c.getResource(imageUri.toString().split("!")[1]));
        else
            image = ImageIO.read(new File(imageUri));

        return bufferedImageToByteArray(image);
    }

    public static Path saveImage(byte[] image, Path folderPath, String imageName) throws IOException {
        if (!imageName.endsWith(".jpg"))
            return null;

        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        Path finalPath = folderPath.resolve(imageName);
        ImageIO.write(bufferedImage, "jpg", finalPath.toFile());
        return finalPath;
    }

}

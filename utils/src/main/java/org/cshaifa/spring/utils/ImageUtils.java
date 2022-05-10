package org.cshaifa.spring.utils;

import java.awt.image.BufferedImage;
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

    public static <T> byte[] getByteArrayFromURI(URI imageUri, Class<T> c) throws IOException {
        BufferedImage image;

        if (imageUri.getScheme().equals("jar"))
            image = ImageIO.read(c.getResource(imageUri.toString().split("!")[1]));
        else
            image = ImageIO.read(new File(imageUri));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream.toByteArray();
    }

}

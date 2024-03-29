package com.importexpress.cart.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jack.luo
 */
@Slf4j
public class SevenZ {

    private SevenZ() {

    }

    /**
     * 压缩多个文件或者目录
     * @param name
     * @param files
     * @throws IOException
     */
    public static void compress(String name, File... files) throws IOException {
        try (SevenZOutputFile out = new SevenZOutputFile(new File(name))){
            for (File file : files){
                addToArchiveCompression(out, file, ".");
            }
        }
    }

    /**
     * 解压缩文件到指定目录位置
     * @param in
     * @param destination
     * @throws IOException
     */
    public static void decompress(String in, File destination) throws IOException {
        SevenZFile sevenZFile = new SevenZFile(new File(in));
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null){
            if (entry.isDirectory()){
                continue;
            }
            File curfile = new File(destination, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try(FileOutputStream out = new FileOutputStream(curfile)) {
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
            }
        }
    }

    private static void addToArchiveCompression(SevenZOutputFile out, File file, String dir) throws IOException {
        String name = dir + File.separator + file.getName();
        if (file.isFile()){
            SevenZArchiveEntry entry = out.createArchiveEntry(file, name);
            out.putArchiveEntry(entry);

            try(FileInputStream in = new FileInputStream(file)) {
                byte[] b = new byte[1024];
                int count = 0;
                while ((count = in.read(b)) > 0) {
                    out.write(b, 0, count);
                }
                out.closeArchiveEntry();
            }

        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null){
                for (File child : children){
                    addToArchiveCompression(out, child, name);
                }
            }
        } else {
            log.warn(file.getName() + " is not supported");
        }
    }
}

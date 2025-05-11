package dev.onelili.bingoreloaded.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class FileUtil {
    public static void deletePath(@NotNull File file) {
        if(file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles != null ? subFiles : new File[0])
                deletePath(subFile);
        } else file.delete();
    }
}

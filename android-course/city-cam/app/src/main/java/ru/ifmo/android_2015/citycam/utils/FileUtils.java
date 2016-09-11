package ru.ifmo.android_2015.citycam.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public final class FileUtils {

    /**
     * Создает временный пустой файл в папке приложения в External Storage
     * Директория: /sdcard/Android/data/<application_package_name>/files/tmp
     *
     * Имя файла — {@code name}.{@code extension}. Файл никак автоматически не удаляется --
     * получатель сам должен позаботиться об удалении после использования.
     *
     * @param context   контекст приложения
     * @param name      имя файла, который будет создан
     * @param extension расширение этого файла (может быть {@code null}, тогда расширение будет .tmp)
     *
     * @return  новый пустой файл {@code name}.{@code extension}
     *
     * @throws IOException  в случае ошибки создания файла.
     */
    public static File createTempExternalFile(Context context, String name, String extension) throws IOException {
        File dir = new File(context.getExternalFilesDir(null), "tmp");
        if (dir.exists() && !dir.isDirectory()) {
            throw new IOException("Not a directory: " + dir);
        }
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory: " + dir);
        }
        return File.createTempFile(name, extension, dir);
    }

    private FileUtils() {}
}

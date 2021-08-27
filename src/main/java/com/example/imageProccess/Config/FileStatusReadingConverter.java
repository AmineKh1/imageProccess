package com.example.imageProccess.Config;


import com.example.imageProccess.model.FileStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class FileStatusReadingConverter implements Converter<String , FileStatus> {
    @Override
    public FileStatus convert(String s) {
        return FileStatus.fromName(s);
    }
}

package com.social.bot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.util.*;

public abstract class AbstractRepository<T> {
    private final ObjectMapper objectMapper;

    protected AbstractRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    protected List<T> load(Class<T[]> clazz, String path) {
        return load(clazz, new File(path));
    }

    @SneakyThrows
    protected List<T> load(Class<T[]> clazz, File file) {
        return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, clazz)));
    }

    @SneakyThrows
    protected void save(List<T> objects, Class<T[]> clazz, String path) {
        save(objects, clazz, path, false);
    }

    @SneakyThrows
    protected void save(List<T> objects, Class<T[]> clazz, String path, boolean rewrite) {
        File file = new File(path);

        if (!file.exists()) {
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            objectMapper.writer().writeValue(file, objects);
            return;
        }

        Set<T> newObjects = new HashSet<>(objects);

        if (!rewrite) {
            Collection<T> savedObjects = Arrays.asList(objectMapper.readValue(file, clazz));
            newObjects.addAll(savedObjects);
        }

        objectMapper.writer().withDefaultPrettyPrinter().writeValue(file, newObjects);
    }
}

package com.example.backend.utils;

import com.example.backend.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryUtils {

    public static void findAllDescendants(Category category, List<UUID> descendants) {
        descendants.add(category.getId());
        for (Category child : category.getSubcategories()) {
            findAllDescendants(child, descendants);
        }
    }
}

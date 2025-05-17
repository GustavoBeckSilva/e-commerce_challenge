package com.compass.e_commerce_challenge.entity;

import java.time.*;
import java.util.*;

public class Category {

    private Long id;

    private String name;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Product> products;
}

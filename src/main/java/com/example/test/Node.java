package com.example.test;

import lombok.Data;
import org.json.simple.JSONArray;

@Data
public class Node {
    private String targetNode;
    private String sourceNode;
    private String selectNode;

    private JSONArray input;
}
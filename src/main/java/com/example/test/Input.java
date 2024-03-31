package com.example.test;

import lombok.Data;

import java.util.List;

@Data
public class Input {
    private List<Node> selectNodeList;
    private List<Node> allNodeList;
}

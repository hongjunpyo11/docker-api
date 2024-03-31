package com.example.test.build;

import com.example.test.Input;
import com.example.test.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class DockerController {
    private List<Integer>[] graph;

    @PostMapping("/findPathToZero")
    public ResponseEntity<List<List<Integer>>> findPathToZero(@RequestBody Input input) {
        List<List<Integer>> allPathsToZero = new ArrayList<>();
        List<Node> selectNodeList = input.getSelectNodeList();
        List<Integer> selectedNodes = new ArrayList<>();
        for (Node node : selectNodeList) {
            selectedNodes.add(Integer.parseInt(node.getSelectNode()));
        }

        List<Node> allNodeList = input.getAllNodeList();

        // 그래프 초기화
        int numNodes = allNodeList.size(); // 주어진 노드의 개수
        graph = new ArrayList[numNodes];
        for (int i = 0; i < numNodes; i++) {
            graph[i] = new ArrayList<>();
        }
        for (Node node : allNodeList) {
            int source = Integer.parseInt(node.getSourceNode());
            int target = Integer.parseInt(node.getTargetNode());
            graph[source].add(target);
        }

        // 선택된 각 노드로부터 0으로 가는 경로 찾기
        for (int node : selectedNodes) {
            boolean[] visited = new boolean[graph.length];
            List<Integer> pathToZero = new ArrayList<>();
            if (dfsToZero(node, visited, pathToZero)) {
                pathToZero.add(node); // 0으로 가는 경로에 해당 노드 추가
                Collections.reverse(pathToZero); // 역순으로 반환
                allPathsToZero.add(pathToZero);
            }
        }

        return new ResponseEntity<>(allPathsToZero, HttpStatus.OK);
    }

    private boolean dfsToZero(int node, boolean[] visited, List<Integer> pathToZero) {
        if (node == 0) {
            return true;
        }
        visited[node] = true;
        for (int neighbor : graph[node]) {
            if (!visited[neighbor] && dfsToZero(neighbor, visited, pathToZero)) {
                System.out.println("neighbor = " + neighbor);
                pathToZero.add(neighbor); // 경로에 해당 노드 추가
                return true;
            }
        }
        return false;
    }


    @PostMapping("/test")
    public ResponseEntity<Map<Integer, List<List<Integer>>>> test(@RequestBody Input input) {
        Map<Integer, List<List<Integer>>> allPathsToZeroMap = new HashMap<>();
        List<Node> allNodeList = input.getAllNodeList();

        // 그래프 초기화
        int numNodes = allNodeList.size(); // 주어진 노드의 개수
        graph = new ArrayList[numNodes];
        for (int i = 0; i < numNodes; i++) {
            graph[i] = new ArrayList<>();
        }
        for (Node node : allNodeList) {
            int source = Integer.parseInt(node.getSourceNode());
            int target = Integer.parseInt(node.getTargetNode());
            graph[source].add(target);
        }

        // 모든 노드로부터 0으로 가는 경로 찾기
        for (int i = 0; i < graph.length; i++) {
            boolean[] visited = new boolean[graph.length];
            List<List<Integer>> pathsToZero = new ArrayList<>();
            dfsAllPathsToZero(i, visited, new ArrayList<>(), pathsToZero);
            allPathsToZeroMap.put(i, pathsToZero);
        }

        return new ResponseEntity<>(allPathsToZeroMap, HttpStatus.OK);
    }

    private void dfsAllPathsToZero(int node, boolean[] visited, List<Integer> currentPath, List<List<Integer>> allPaths) {
        visited[node] = true;
        currentPath.add(node);
        if (node == 0) {
            // 0에 도달한 경우 현재 경로를 결과에 추가하고 종료
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // 인접한 노드로 DFS 수행
            for (int neighbor : graph[node]) {
                if (!visited[neighbor]) {
                    dfsAllPathsToZero(neighbor, visited, currentPath, allPaths);
                }
            }
        }
        // 백트래킹
        visited[node] = false;
        currentPath.remove(currentPath.size() - 1);
    }
}

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


    @PostMapping("/testv2")
    public ResponseEntity<Map<Integer, List<List<Integer>>>> testv2(@RequestBody Input input) {
        Map<Integer, List<List<Integer>>> allPathsToZeroMap = new HashMap<>();
        List<Node> allNodeList = input.getAllNodeList();
        System.out.println("allNodeList.size() = " + allNodeList.size());

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

    @PostMapping("/findShortestPathToZero")
    public ResponseEntity<List<List<Integer>>> findShortestPathToZero(@RequestBody Input input) {
        List<Node> selectNodeList = input.getSelectNodeList();
        List<Node> allNodeList = input.getAllNodeList();

        // 그래프 초기화
        List<List<Integer>> graph = initializeGraph(allNodeList);

        // 선택된 각 노드로부터 0으로 가는 최단 경로 찾기
        List<List<Integer>> allShortestPathsToZero = new ArrayList<>();
        for (Node selectNode : selectNodeList) {
            int startNode = Integer.parseInt(selectNode.getSelectNode());
            List<Integer> shortestPathToZero = findShortestPathToZero(graph, startNode);
            allShortestPathsToZero.add(shortestPathToZero);
        }

        return new ResponseEntity<>(allShortestPathsToZero, HttpStatus.OK);
    }

    private List<List<Integer>> initializeGraph(List<Node> allNodeList) {
        int numNodes = calculateNumNodes(allNodeList);
        List<List<Integer>> graph = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            graph.add(new ArrayList<>());
        }
        for (Node node : allNodeList) {
            int source = Integer.parseInt(node.getSourceNode());
            int target = Integer.parseInt(node.getTargetNode());
            graph.get(source).add(target);
            graph.get(target).add(source); // 양방향 통신이므로 반대 방향도 추가
        }
        return graph;
    }

    private int calculateNumNodes(List<Node> allNodeList) {
        int maxNode = 0;
        for (Node node : allNodeList) {
            int source = Integer.parseInt(node.getSourceNode());
            int target = Integer.parseInt(node.getTargetNode());
            maxNode = Math.max(maxNode, Math.max(source, target));
        }
        return maxNode + 1;
    }

    private List<Integer> findShortestPathToZero(List<List<Integer>> graph, int startNode) {
        int numNodes = graph.size();
        boolean[] visited = new boolean[numNodes];
        int[] distance = new int[numNodes];
        int[] parent = new int[numNodes];

        // 초기화
        for (int i = 0; i < numNodes; i++) {
            distance[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        // 너비 우선 탐색
        List<Integer> shortestPath = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        visited[startNode] = true;
        distance[startNode] = 0;

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            if (currentNode == 0) { // 0에 도착하면 최단 경로 반환
                int node = 0; // 목적지 노드
                while (node != startNode) {
                    shortestPath.add(node);
                    node = parent[node];
                }
                shortestPath.add(startNode);
                Collections.reverse(shortestPath);
                return shortestPath;
            }
            for (int neighbor : graph.get(currentNode)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[currentNode] + 1;
                    parent[neighbor] = currentNode;
                    queue.add(neighbor);
                }
            }
        }

        return shortestPath; // 0에 도달하지 못한 경우 빈 리스트 반환
    }

}

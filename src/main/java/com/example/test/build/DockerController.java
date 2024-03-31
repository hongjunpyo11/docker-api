package com.example.test.build;

import com.example.test.Input;
import com.example.test.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DockerController {
    private List<Integer>[] graph;

    @PostMapping("/findPathToZero")
    public ResponseEntity<List<Integer>> findPathToZero(@RequestBody Input input) {
        List<Integer> pathToZero = new ArrayList<>();
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

        // 0으로 도달 가능한지 확인하기 위해 선택된 노드들로부터 DFS 수행
        for (int node : selectedNodes) {
            boolean[] visited = new boolean[graph.length];
            if (dfsToZero(node, visited, pathToZero)) {
                pathToZero.add(node); // 0으로 가는 경로에 해당 노드 추가
                Collections.reverse(pathToZero); // 역순으로 반환
                return new ResponseEntity<>(pathToZero, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 경로를 찾지 못한 경우
    }

    private boolean dfsToZero(int node, boolean[] visited, List<Integer> pathToZero) {
        if (node == 0) {
            return true;
        }
        visited[node] = true;
        for (int neighbor : graph[node]) {
            if (!visited[neighbor] && dfsToZero(neighbor, visited, pathToZero)) {
                pathToZero.add(neighbor); // 경로에 해당 노드 추가
                return true;
            }
        }
        return false;
    }
}

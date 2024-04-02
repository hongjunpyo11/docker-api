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

@RestController()
@RequiredArgsConstructor
public class TestController {

    private List<List<Integer>> graph;

    @PostMapping("/findPathToZeroV2")
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
        graph = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            graph.add(new ArrayList<>());
        }
        for (Node node : allNodeList) {
            int source = Integer.parseInt(node.getSourceNode());
            int target = Integer.parseInt(node.getTargetNode());
            graph.get(source).add(target);
        }

        // 선택된 각 노드로부터 0으로 가는 경로 찾기
        for (int node : selectedNodes) {
            boolean[] visited = new boolean[graph.size()];
            List<Integer> pathToZero = new ArrayList<>();
            if (dfsToZero(node, visited, pathToZero)) {
                pathToZero.add(node); // 0으로 가는 경로에 해당 노드 추가
                Collections.reverse(pathToZero); // 역순으로 반환
                allPathsToZero.add(pathToZero);
            }
        }

        // 중복된 경로 제거
        List<List<Integer>> allFilteredShortestPathsToZero = removeSubsets(allPathsToZero);

        return new ResponseEntity<>(allFilteredShortestPathsToZero, HttpStatus.OK);
    }

    private boolean dfsToZero(int node, boolean[] visited, List<Integer> pathToZero) {
        if (node == 0) {
            return true;
        }
        visited[node] = true;
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor] && dfsToZero(neighbor, visited, pathToZero)) {
                pathToZero.add(neighbor); // 경로에 해당 노드 추가
                return true;
            }
        }
        return false;
    }

    private List<List<Integer>> removeSubsets(List<List<Integer>> paths) {
        List<List<Integer>> filteredPaths = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            boolean isSubset = false;
            List<Integer> currentPath = paths.get(i);
            for (int j = 0; j < paths.size(); j++) {
                if (i != j) {
                    List<Integer> otherPath = paths.get(j);
                    if (isSubset(currentPath, otherPath)) {
                        isSubset = true;
                        break;
                    }
                }
            }
            if (!isSubset) {
                filteredPaths.add(currentPath);
            }
        }
        return filteredPaths;
    }

    private boolean isSubset(List<Integer> path1, List<Integer> path2) {
        int index1 = 0;
        int index2 = 0;
        while (index1 < path1.size() && index2 < path2.size()) {
            if (path1.get(index1).equals(path2.get(index2))) {
                index1++;
            }
            index2++;
        }
        return index1 == path1.size();
    }
}
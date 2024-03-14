package com.example.test.build;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DockerController {

    private final DockerClient dockerClient;

    @RequestMapping("test")
    public String test() {
        try {
            DockerClient dockerClient = DockerClientBuilder.getInstance().build();

//            ByteArrayInputStream dockerfileStream = new ByteArrayInputStream(dockerfileContent.getBytes(StandardCharsets.UTF_8));
            String dockerfilePath = "C:\\Users\\82103\\Documents\\temp\\Dockerfile";

            String imageId = dockerClient.buildImageCmd()
                    .withDockerfile(new File(dockerfilePath))
                    .exec(new BuildImageResultCallback() {
                        @Override
                        public void onNext(BuildResponseItem item) {
                            // Docker 빌드 진행 상황 로깅
                            System.out.println(item.getStream());
                            super.onNext(item);
                        }
                    })
                    .awaitImageId();

            dockerClient.pushImageCmd(imageId)
                    .exec(new PushImageResultCallback())
                    .awaitSuccess();

            return "Docker image built and pushed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to build and push Docker image: " + e.getMessage();
        }
    }

    @RequestMapping("/test2")
    public ResponseEntity<String> buildAndPushImage() throws InterruptedException {
        buildAndPushImage("C:\\Users\\82103\\Documents\\temp\\Dockerfile", "demo", "0.2");
        return ResponseEntity.ok("이미지가 성공적으로 빌드되고 푸시되었습니다.");
    }

    public void buildAndPushImage(String dockerfilePath, String imageName, String tag) throws InterruptedException {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2376")
                .withDockerTlsVerify(false)
                .build();

        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        // 이미지 빌드
        dockerClient.buildImageCmd(Paths.get(dockerfilePath).toFile())
                .withTag(imageName + ":" + tag)
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        // Registry 인증 정보 설정
//        AuthConfig authConfig = new AuthConfig()
//                .withUsername(registryUsername)
//                .withPassword(registryPassword)
//                .withRegistryAddress(registryUrl);
//
//        // 이미지 Push
//        dockerClient.pushImageCmd(imageName)
//                .withTag(tag)
//                .withAuthConfig(authConfig)
//                .exec(new PushImageResultCallback())
//                .awaitCompletion();
    }

    @RequestMapping("/test3")
    public String test3() {
        List<Image> exec = dockerClient.listImagesCmd()
                .withShowAll(true)
                .exec();
        for (Image image : exec) {
            System.out.println("image.toString() = " + image.toString());
        }
        return "ok";
    }
}

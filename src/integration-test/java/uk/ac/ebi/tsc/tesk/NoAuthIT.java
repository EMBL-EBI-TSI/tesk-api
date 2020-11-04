package uk.ac.ebi.tsc.tesk;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.tsc.tesk.TestUtils.getFileContentFromResources;

/**
 * @author Ania Niewielska <aniewielska@ebi.ac.uk>
 * <p>
 * Integration testing of switched off security
 * Kubernetes API is WireMocked
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = {"classpath:application.properties"},
        properties = {"security.oauth2.resource.user-info-uri = http://localhost:8090",
                "spring.profiles.active=noauth","tesk.api.authorisation.ignoreGroupMembership=false"})
public class NoAuthIT {

    @Autowired
    private MockMvc mvc;

    @Rule
    public WireMockRule mockKubernetes = new WireMockRule(wireMockConfig().port(9000).usingFilesUnderDirectory("src/integration-test/resources"));

    @TestConfiguration
    static class KubernetesClientMock {
        @Bean
        @Primary
        public ApiClient kubernetesApiClient() {

            return Config.fromUrl("http://localhost:9000", false);

        }
    }

    @Test
    public void createTask() throws Exception {

        mockKubernetes.givenThat(
                WireMock.post("/apis/batch/v1/namespaces/default/jobs")
                        .withRequestBody(matchingJsonPath("$.metadata.labels['creator-group-name']", absent()))
                        .withRequestBody(matchingJsonPath("$.metadata.labels[?(@.creator-user-id == 'anonymousUser')]"))
                        .willReturn(okJson("{\"metadata\":{\"name\":\"task-fe99716a\"}}")));

        String path = "fromTesToK8s_minimal/task.json";
        this.mvc.perform(post("/v1/tasks")
                .content(getFileContentFromResources(path))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getTask() throws Exception {

        MockUtil.mockGetTaskKubernetesResponses(this.mockKubernetes);

        this.mvc.perform(get("/v1/tasks/{id}", "task-123"))
                .andExpect(status().isOk());
        this.mvc.perform(get("/v1/tasks/{id}?view=BASIC", "task-123"))
                .andExpect(status().isOk());
        this.mvc.perform(get("/v1/tasks/{id}?view=FULL", "task-123"))
                .andExpect(status().isOk());
    }

    @Test
    public void cancelTask() throws Exception {

        MockUtil.mockGetTaskKubernetesResponses(this.mockKubernetes);

        this.mvc.perform(post("/v1/tasks/{id}:cancel", "task-123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Job with ID=task-123 has no pods in RUNNING status."));

    }

    @Test
    public void getList() throws Exception {

        mockKubernetes.givenThat(
                WireMock.get("/apis/batch/v1/namespaces/default/jobs?labelSelector=job-type%3Dtaskmaster")
                        .willReturn(aResponse().withBodyFile("list/taskmasters.json")));
        MockUtil.mockListTaskKubernetesResponses(this.mockKubernetes);

        this.mvc.perform(get("/v1/tasks")
                .header("Authorization", "Bearer BAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks.length()").value(4));
        this.mvc.perform(get("/v1/tasks?view=BASIC")
                .header("Authorization", "Bearer BAR"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tasks.length()").value(4));
        this.mvc.perform(get("/v1/tasks?view=FULL")
                .header("Authorization", "Bearer BAR"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tasks.length()").value(4));
    }
    @Test
    public void serviceInfo() throws Exception {
        this.mvc.perform(get("/v1/tasks/service-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TESK in EBI"))
                .andExpect(jsonPath("$.doc").value("https://github.com/EMBL-EBI-TSI/TESK"));
    }


}

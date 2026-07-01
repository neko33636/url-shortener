package com.maksim.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UrlShortenerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void createUrlWithoutAuthReturns401() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUrlWithAuthReturns200AndRedirectWorks() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"secret\"}"))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult createResult = mockMvc.perform(post("/api/urls")
                        .session(loginResult.getRequest().getSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.org\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn();

        String shortUrl = com.jayway.jsonpath.JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.shortUrl");
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

        mockMvc.perform(get("/r/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(result -> {
                    if (!"https://example.org".equals(result.getResponse().getHeader("Location"))) {
                        throw new AssertionError("Unexpected redirect location");
                    }
                });
    }

    @Test
    void listUrlsWithoutAuthReturns200() throws Exception {
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk());
    }

    @Test
    void listUrlsAfterCreateContainsEntry() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"listuser\",\"password\":\"secret\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(post("/api/auth/login")
                        .session(loginResult.getRequest().getSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"listuser\",\"password\":\"secret\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/urls")
                        .session(loginResult.getRequest().getSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://list-test.com\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalUrl").value("https://list-test.com"))
                .andExpect(jsonPath("$[0].ownerUsername").value("listuser"));
    }
}

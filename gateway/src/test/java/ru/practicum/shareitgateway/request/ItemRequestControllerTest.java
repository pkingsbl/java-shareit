package ru.practicum.shareitgateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;
import java.nio.charset.StandardCharsets;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final String HEADER_ID = "X-Sharer-User-Id";
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();


    @Test
    void testAdd() throws Exception {
        when(itemRequestClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/requests")
                        .header(HEADER_ID, USER_ID)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddWithoutUserId() throws Exception {
        when(itemRequestClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testAddWithoutBody() throws Exception {
        when(itemRequestClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/requests")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testGetAllOwner() throws Exception {
        when(itemRequestClient.getAllOwner(anyLong()))
                .thenReturn(null);

        mvc.perform(get("/requests")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOwnerWithoutUserId() throws Exception {
        when(itemRequestClient.getAllOwner(anyLong()))
                .thenReturn(null);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetAll() throws Exception {
        when(itemRequestClient.getAllFrom(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/requests/all")
                        .header(HEADER_ID, USER_ID)
                        .param("from", "23")
                        .param("size", "64")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllWithoutUserId() throws Exception {
        when(itemRequestClient.getAllFrom(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/requests/all")
                        .param("from", "23")
                        .param("size", "64")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetAllWithoutParam() throws Exception {
        when(itemRequestClient.getAllFrom(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/requests/all")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        when(itemRequestClient.getById(anyLong(), anyLong()))
                .thenReturn(null);

        mvc.perform(get("/requests/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdWithoutUserId() throws Exception {
        when(itemRequestClient.getById(anyLong(), anyLong()))
                .thenReturn(null);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

}
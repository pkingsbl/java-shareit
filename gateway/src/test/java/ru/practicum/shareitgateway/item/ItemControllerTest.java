package ru.practicum.shareitgateway.item;

import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final String HEADER_ID = "X-Sharer-User-Id";
    private final ItemDto itemDto = ItemDto.builder()
            .name("Cuckoo")
            .description("For your watch")
            .available(true)
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .text("шикарно")
            .build();

    @Test
    void testAddCorrect() throws Exception {
        when(itemClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items")
                        .header(HEADER_ID, USER_ID)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddWithoutUserId() throws Exception {
        when(itemClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testAddWithoutBody() throws Exception {
        when(itemClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testChange() throws Exception {
        when(itemClient.change(anyLong(), any(),  any()))
                .thenReturn(null);

        mvc.perform(patch("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testChangeWithoutUserId() throws Exception {
        when(itemClient.change(anyLong(), any(),  any()))
                .thenReturn(null);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testChangeWithoutBody() throws Exception {
        when(itemClient.change(anyLong(), any(),  any()))
                .thenReturn(null);

        mvc.perform(patch("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testGetById() throws Exception {
        when(itemClient.getById(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(get("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdWithoutUserId() throws Exception {
        when(itemClient.getById(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetAll() throws Exception {
        when(itemClient.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items")
                        .header(HEADER_ID, USER_ID)
                        .param("from", "2")
                        .param("size", "6")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllWithoutUserId() throws Exception {
        when(itemClient.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items")
                        .param("from", "2")
                        .param("size", "6")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetAllWithoutParam() throws Exception {
        when(itemClient.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSearch() throws Exception {
        when(itemClient.getSearch(anyLong(), anyString(), anyInt(),anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items/search")
                        .header(HEADER_ID, USER_ID)
                        .param("text", "gg")
                        .param("from", "2")
                        .param("size", "6")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSearchWithoutUserId() throws Exception {
        when(itemClient.getSearch(anyLong(), anyString(), anyInt(),anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items/search")
                        .param("text", "gg")
                        .param("from", "2")
                        .param("size", "6")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetSearchWithoutParam() throws Exception {
        when(itemClient.getSearch(anyLong(), anyString(), anyInt(),anyInt()))
                .thenReturn(null);

        mvc.perform(get("/items/search")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteById() throws Exception {
        mvc.perform(delete("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteByIdWithoutUserId() throws Exception {
        mvc.perform(delete("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemClient.postComment(anyLong(), anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_ID, USER_ID)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddCommentWithoutUserId() throws Exception {
        when(itemClient.postComment(anyLong(), anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testAddCommentWithoutBody() throws Exception {
        when(itemClient.postComment(anyLong(), anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

}
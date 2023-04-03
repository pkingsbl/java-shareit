package ru.practicum.shareitserver.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final String HEADER_ID = "X-Sharer-User-Id";
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Cuckoo")
            .description("For your watch")
            .available(true)
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .text("шикарно")
            .build();

    @Test
    void testAddCorrect() throws Exception {
        when(itemService.add(anyLong(), any()))
                .thenReturn(itemDto);

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
        when(itemService.add(anyLong(), any()))
                .thenReturn(itemDto);

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
        when(itemService.add(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testChange() throws Exception {
        when(itemService.change(anyLong(), any(),  any()))
                .thenReturn(itemDto);

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
        when(itemService.change(anyLong(), any(),  any()))
                .thenReturn(itemDto);

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
        when(itemService.change(anyLong(), any(),  any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testGetById() throws Exception {
        when(itemService.getById(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdWithoutUserId() throws Exception {
        when(itemService.getById(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testGetAll() throws Exception {
        when(itemService.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemDto));

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
        when(itemService.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemDto));

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
        when(itemService.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSearch() throws Exception {
        when(itemService.getSearch(any(), any(), any()))
                .thenReturn(List.of(itemDto));

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
        when(itemService.getSearch(any(), any(), any()))
                .thenReturn(List.of(itemDto));

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
        when(itemService.getSearch(any(), any(), any()))
                .thenReturn(List.of(itemDto));

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
        when(itemService.postComment(anyLong(), any(), any()))
                .thenReturn(commentDto);

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
        when(itemService.postComment(anyLong(), any(), any()))
                .thenReturn(commentDto);

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
        when(itemService.postComment(anyLong(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

}
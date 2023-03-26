package ru.practicum.shareitgateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.booking.dto.BookingDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final String HEADER_ID = "X-Sharer-User-Id";
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final BookingDto bookingDto = BookingDto.builder()
            .start(dateTime.plusDays(1))
            .end(dateTime.plusDays(2))
            .itemId(1L)
            .build();

    @Test
    void testAddCorrect() throws Exception {
        when(bookingClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddWithoutUserId() throws Exception {
        when(bookingClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testAddWithoutBody() throws Exception {
        when(bookingClient.add(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(post("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testApprove() throws Exception {
        when(bookingClient.approve(anyLong(), any(), any()))
                .thenReturn(null);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER_ID, USER_ID)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testApproveWithoutUserId() throws Exception {
        when(bookingClient.approve(anyLong(), any(), any()))
                .thenReturn(null);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testApproveWithoutRequestParam() throws Exception {
        when(bookingClient.approve(anyLong(), any(), any()))
                .thenReturn(null);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request parameter 'approved' for method parameter type Boolean is not present")));
    }

    @Test
    void testFindById() throws Exception {
        when(bookingClient.findById(anyLong(), anyLong()))
                .thenReturn(null);

        mvc.perform(get("/bookings/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByIdWithoutUserId() throws Exception {
        when(bookingClient.findById(anyLong(), anyLong()))
                .thenReturn(null);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }


    @Test
    void testFindAllByUser() throws Exception {
        when(bookingClient.findAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .param("state", "ALL")
                        .param("from", "4")
                        .param("size", "36")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllByUserWithoutUserId() throws Exception {
        when(bookingClient.findAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "4")
                        .param("size", "36")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testFindAllByUserWithoutParam() throws Exception {
        when(bookingClient.findAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllByOwner() throws Exception {
        when(bookingClient.findAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_ID, USER_ID)
                        .param("state", "ALL")
                        .param("from", "8")
                        .param("size", "15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllByOwnerWithoutUserId() throws Exception {
        when(bookingClient.findAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "8")
                        .param("size", "15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testFindAllByOwnerWithoutParam() throws Exception {
        when(bookingClient.findAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(null);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
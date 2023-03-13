package ru.practicum.shareit.booking;

import java.util.List;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID = 1L;
    private static final String HEADER_ID = "X-Sharer-User-Id";
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final Booking booking = Booking
            .builder()
            .id(1L)
            .start(dateTime.plusDays(1).plusSeconds(1))
            .end(dateTime.plusDays(4).plusSeconds(1))
            .item(Item.builder().id(1L).build())
            .booker(new User(USER_ID, "Name", "mail@mail.com"))
            .status(Status.WAITING)
            .build();

    private final BookingDto bookingDto = BookingDto
            .builder()
            .start(dateTime.plusDays(1).plusSeconds(1))
            .end(dateTime.plusDays(4).plusSeconds(1))
            .itemId(booking.getItem().getId())
            .build();

    @Test
    void testAddCorrect() throws Exception {
        when(bookingService.add(anyLong(), any()))
                .thenReturn(booking);

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
        when(bookingService.add(anyLong(), any()))
                .thenReturn(booking);

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
        when(bookingService.add(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testApprove() throws Exception {
        when(bookingService.approve(anyLong(), any(), any()))
                .thenReturn(booking);

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
        when(bookingService.approve(anyLong(), any(), any()))
                .thenReturn(booking);

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
        when(bookingService.approve(anyLong(), any(), any()))
                .thenReturn(booking);

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
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByIdWithoutUserId() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void testFindAllByUser() throws Exception {
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

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
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

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
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllByOwner() throws Exception {
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

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
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

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
        when(bookingService.findAllByUser(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_ID, USER_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
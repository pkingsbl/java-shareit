package ru.practicum.shareit.item.model;

import javax.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;

@Data
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "id_owner", referencedColumnName = "id", nullable = false)
    private User owner;

    @Transient
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private BookingDto lastBooking;

    @Transient
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private BookingDto nextBooking;

}

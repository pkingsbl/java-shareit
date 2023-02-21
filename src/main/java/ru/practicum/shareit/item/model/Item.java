package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


/**
 * TODO Sprint add-controllers.
 * id — уникальный идентификатор вещи;
 * name — краткое название;
 * description — развёрнутое описание;
 * available — статус о том, доступна или нет вещь для аренды;
 * owner — владелец вещи;
 * request — если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.
 */
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
//    @ManyToOne
//    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Booking request;

}

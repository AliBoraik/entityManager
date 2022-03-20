package ru.itis.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.orm.annotations.Column;
import ru.itis.orm.annotations.Entity;
import ru.itis.orm.annotations.Id;

@Data
@NoArgsConstructor
@Entity(name = "cars")
public class Car {

    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "car_number")
    String number;

    @Column(name = "model_id")
    Long model;

}

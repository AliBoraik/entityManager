package ru.itis.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.orm.annotations.Column;
import ru.itis.orm.annotations.Entity;
import ru.itis.orm.annotations.Id;

@Data
@NoArgsConstructor
@Entity(name = "model")
public class Model {
    @Id
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
}
